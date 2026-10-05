package com.edunest;

import com.edunest.dto.request.LearningSessionRequest;
import com.edunest.dto.request.LoginRequest;
import com.edunest.dto.request.QuizSubmissionRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RevisionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String loginAsStudent() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("student@edunest.com");
        req.setPassword("password123");

        MvcResult res = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(res.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    void testSpacedRevisionScheduleAndDailyPlanFlow() throws Exception {
        String token = loginAsStudent();

        // 1. Get Java Course ID
        MvcResult catalogRes = mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andReturn();
        Long courseId = objectMapper.readTree(catalogRes.getResponse().getContentAsString()).get(0).get("id").asLong();

        // 2. Enroll in Course so DailyPlanService reads enrolled course
        mockMvc.perform(post("/api/courses/" + courseId + "/enroll")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 3. Submit Diagnostic with Topic 5 (Inheritance) wrong -> auto-schedules Stage 1 revision
        MvcResult diagRes = mockMvc.perform(get("/api/quizzes/diagnostic/" + courseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode diagJson = objectMapper.readTree(diagRes.getResponse().getContentAsString());
        Long diagQuizId = diagJson.get("quizId").asLong();
        JsonNode questions = diagJson.get("questions");
        Long inheritanceTopicId = questions.get(4).get("topicId").asLong();

        List<QuizSubmissionRequest.QuestionAnswer> answers = new ArrayList<>();
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(0).get("id").asLong(), "A"));
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(1).get("id").asLong(), "C"));
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(2).get("id").asLong(), "C"));
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(3).get("id").asLong(), "C"));
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(4).get("id").asLong(), "A")); // Wrong -> WEAK
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(5).get("id").asLong(), "C"));

        QuizSubmissionRequest subReq = new QuizSubmissionRequest();
        subReq.setQuizId(diagQuizId);
        subReq.setAnswers(answers);

        mockMvc.perform(post("/api/quizzes/submit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subReq)))
                .andExpect(status().isOk());

        // 4. Verify pending RevisionSchedule exists for Inheritance
        MvcResult revRes = mockMvc.perform(get("/api/students/me/revision-schedule?onlyPending=true")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty())
                .andReturn();

        JsonNode revArr = objectMapper.readTree(revRes.getResponse().getContentAsString());
        Long revisionId = null;
        for (JsonNode item : revArr) {
            if (item.get("topicId").asLong() == inheritanceTopicId) {
                revisionId = item.get("id").asLong();
                break;
            }
        }
        org.junit.jupiter.api.Assertions.assertNotNull(revisionId, "Revision task should be auto-scheduled for weak topic");

        // 5. Verify Daily Plan includes actionable items (up to 5 items)
        mockMvc.perform(get("/api/students/me/daily-plan")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty())
                .andExpect(jsonPath("$[0].order").value(1));

        // 6. Complete Stage 1 Revision with 100% score -> updates Revision Score in TopicMastery and schedules next stage
        mockMvc.perform(post("/api/students/me/revision-schedule/" + revisionId + "/complete")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("score", 100.0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.revisionScore").value(100.0));

        // 7. Verify Knowledge Profile shows revisionScore = 100.0 for Inheritance
        mockMvc.perform(get("/api/students/me/courses/" + courseId + "/knowledge-profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topics[4].revisionScore").value(100.0));

        // 8. Log a study session and verify session retrieval
        LearningSessionRequest sessionReq = new LearningSessionRequest();
        sessionReq.setCourseId(courseId);
        sessionReq.setActivityType("SPACED_REVISION");
        sessionReq.setDurationMinutes(20);

        mockMvc.perform(post("/api/students/me/sessions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activityType").value("SPACED_REVISION"))
                .andExpect(jsonPath("$.durationMinutes").value(20));

        mockMvc.perform(get("/api/students/me/sessions")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty());
    }
}
