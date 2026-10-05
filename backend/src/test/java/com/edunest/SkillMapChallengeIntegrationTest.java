package com.edunest;

import com.edunest.dto.request.ChallengeSubmissionRequest;
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
public class SkillMapChallengeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String registerFreshStudent() throws Exception {
        com.edunest.dto.request.RegisterRequest req = new com.edunest.dto.request.RegisterRequest();
        req.setName("SkillMap Tester");
        req.setEmail("skillmap_student@edunest.com");
        req.setPassword("password123");
        req.setRole(com.edunest.enums.Role.STUDENT);
        req.setLearningGoal("Master Java Challenges");

        MvcResult res = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(res.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    void testVisualSkillMapUnlockAndPracticalChallengeSubmission() throws Exception {
        String token = registerFreshStudent();

        // 1. Get Java Course ID
        MvcResult catalogRes = mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andReturn();
        Long courseId = objectMapper.readTree(catalogRes.getResponse().getContentAsString()).get(0).get("id").asLong();

        // 2. Submit Diagnostic Quiz: Topic 1 (Variables) = 100% (MASTERED), Topic 5 (Inheritance) = 0% (WEAK)
        MvcResult diagRes = mockMvc.perform(get("/api/quizzes/diagnostic/" + courseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode diagJson = objectMapper.readTree(diagRes.getResponse().getContentAsString());
        Long diagQuizId = diagJson.get("quizId").asLong();
        JsonNode questions = diagJson.get("questions");

        List<QuizSubmissionRequest.QuestionAnswer> answers = new ArrayList<>();
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(0).get("id").asLong(), "A")); // Variables: 100% -> GOLD
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(1).get("id").asLong(), "C")); // Loops: 100% -> GOLD
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(2).get("id").asLong(), "C")); // Arrays: 100% -> GOLD
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(3).get("id").asLong(), "C")); // OOP: 100% -> GOLD
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(4).get("id").asLong(), "A")); // Inheritance: 0% -> RED
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(5).get("id").asLong(), "C")); // Collections: 100% -> GOLD

        QuizSubmissionRequest subReq = new QuizSubmissionRequest();
        subReq.setQuizId(diagQuizId);
        subReq.setAnswers(answers);

        mockMvc.perform(post("/api/quizzes/submit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subReq)))
                .andExpect(status().isOk());

        // 3. Verify Visual Skill Map shows GOLD unlocked node for Topic 1 and RED locked node for Topic 5
        MvcResult mapRes = mockMvc.perform(get("/api/students/me/courses/" + courseId + "/skill-map")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unlockedChallengesCount").value(5))
                .andExpect(jsonPath("$.entries[0].colorCode").value("GOLD"))
                .andExpect(jsonPath("$.entries[0].challengeUnlocked").value(true))
                .andExpect(jsonPath("$.entries[4].colorCode").value("RED"))
                .andExpect(jsonPath("$.entries[4].challengeUnlocked").value(false))
                .andReturn();

        JsonNode mapJson = objectMapper.readTree(mapRes.getResponse().getContentAsString());
        Long unlockedChallengeId = mapJson.get("entries").get(0).get("challengeId").asLong();
        Long lockedChallengeId = mapJson.get("entries").get(4).get("challengeId").asLong();

        // 4. Attempting to submit a locked challenge (Topic 5 < 90% mastery) returns 400 Bad Request
        ChallengeSubmissionRequest lockedAttempt = new ChallengeSubmissionRequest("class UpiPayment extends Payment { @Override public void process() {} }");
        mockMvc.perform(post("/api/challenges/" + lockedChallengeId + "/submit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lockedAttempt)))
                .andExpect(status().isBadRequest());

        // 5. Submitting a valid Java solution to the unlocked Topic 1 challenge passes rule-based evaluation (100%)
        ChallengeSubmissionRequest validSub = new ChallengeSubmissionRequest(
                "public class PriceCalculator { public static void main(String[] args) { final double TAX_RATE = 0.18; int total = (int) (100 * (1 + TAX_RATE)); } }"
        );
        mockMvc.perform(post("/api/challenges/" + unlockedChallengeId + "/submit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSub)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passed").value(true))
                .andExpect(jsonPath("$.score").value(100.0));

        // 6. Verify Skill Map now records completedChallengesCount = 1 and my-submissions endpoint lists it
        mockMvc.perform(get("/api/students/me/courses/" + courseId + "/skill-map")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completedChallengesCount").value(1))
                .andExpect(jsonPath("$.entries[0].challengeCompleted").value(true));

        mockMvc.perform(get("/api/challenges/my-submissions")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].passed").value(true));
    }
}
