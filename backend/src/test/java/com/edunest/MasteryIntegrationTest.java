package com.edunest;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MasteryIntegrationTest {

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
    void testKnowledgeProfileAndAdaptiveLearningPathAfterDiagnosticAndQuiz() throws Exception {
        String token = loginAsStudent();

        // 1. Get Java Course ID
        MvcResult catalogRes = mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andReturn();
        Long courseId = objectMapper.readTree(catalogRes.getResponse().getContentAsString()).get(0).get("id").asLong();

        // 2. Load Diagnostic Quiz and answer all right EXCEPT Topic 5 (Inheritance)
        MvcResult diagRes = mockMvc.perform(get("/api/quizzes/diagnostic/" + courseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode diagJson = objectMapper.readTree(diagRes.getResponse().getContentAsString());
        Long diagQuizId = diagJson.get("quizId").asLong();
        JsonNode questions = diagJson.get("questions");
        Long inheritanceTopicId = questions.get(4).get("topicId").asLong();

        List<QuizSubmissionRequest.QuestionAnswer> answers = new ArrayList<>();
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(0).get("id").asLong(), "A")); // Variables: 100%
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(1).get("id").asLong(), "C")); // Loops: 100%
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(2).get("id").asLong(), "C")); // Arrays: 100%
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(3).get("id").asLong(), "C")); // OOP: 100%
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(4).get("id").asLong(), "A")); // Inheritance: 0% (WEAK)
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(5).get("id").asLong(), "C")); // Collections: 100%

        QuizSubmissionRequest subReq = new QuizSubmissionRequest();
        subReq.setQuizId(diagQuizId);
        subReq.setAnswers(answers);

        mockMvc.perform(post("/api/quizzes/submit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subReq)))
                .andExpect(status().isOk());

        // 3. Verify Knowledge Profile shows 5 MASTERED topics and 1 WEAK topic (Inheritance)
        mockMvc.perform(get("/api/students/me/courses/" + courseId + "/knowledge-profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weakCount").value(1))
                .andExpect(jsonPath("$.masteredCount").value(5))
                .andExpect(jsonPath("$.topics[4].topicTitle").value("Inheritance & Polymorphism"))
                .andExpect(jsonPath("$.topics[4].masteryLevel").value("WEAK"));

        // 4. Verify Personalized Learning Path prioritizes the WEAK topic (Inheritance) as Step 1 (READ_LESSON, HIGH priority)
        mockMvc.perform(get("/api/students/me/courses/" + courseId + "/learning-path")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stepOrder").value(1))
                .andExpect(jsonPath("$[0].topicTitle").value("Inheritance & Polymorphism"))
                .andExpect(jsonPath("$[0].actionType").value("READ_LESSON"))
                .andExpect(jsonPath("$[0].priority").value("HIGH"));

        // 5. Student takes Chapter Quiz on Inheritance and scores 100%
        MvcResult chapterRes = mockMvc.perform(get("/api/quizzes/chapter/" + inheritanceTopicId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode chapterJson = objectMapper.readTree(chapterRes.getResponse().getContentAsString());
        Long chapterQuizId = chapterJson.get("quizId").asLong();
        Long chapterQuestionId = chapterJson.get("questions").get(0).get("id").asLong();

        QuizSubmissionRequest chapterSub = new QuizSubmissionRequest();
        chapterSub.setQuizId(chapterQuizId);
        chapterSub.setAnswers(List.of(new QuizSubmissionRequest.QuestionAnswer(chapterQuestionId, "B"))); // Correct answer='B'

        mockMvc.perform(post("/api/quizzes/submit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chapterSub)))
                .andExpect(status().isOk());

        // 6. Verify Inheritance mastery rose from 0% (WEAK) to 66.7% (NEEDS_WORK) via weighted formula (Diag 20% [0] + Quiz 40% [100])
        mockMvc.perform(get("/api/students/me/courses/" + courseId + "/knowledge-profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topics[4].masteryScore").value(66.7))
                .andExpect(jsonPath("$.topics[4].masteryLevel").value("NEEDS_WORK"));
    }
}
