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
public class QuizIntegrationTest {

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
    void testDiagnosticPracticeAndQuizFlowWithTopicBreakdown() throws Exception {
        String token = loginAsStudent();

        // 1. Get the Java Course ID
        MvcResult catalogRes = mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andReturn();
        Long courseId = objectMapper.readTree(catalogRes.getResponse().getContentAsString()).get(0).get("id").asLong();

        // 2. Load Diagnostic Quiz for the course
        MvcResult diagRes = mockMvc.perform(get("/api/quizzes/diagnostic/" + courseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizType").value("DIAGNOSTIC"))
                .andExpect(jsonPath("$.questions.length()").value(6))
                .andExpect(jsonPath("$.questions[0].correctOption").isEmpty()) // Hidden during diagnostic
                .andReturn();

        JsonNode diagJson = objectMapper.readTree(diagRes.getResponse().getContentAsString());
        Long diagQuizId = diagJson.get("quizId").asLong();
        JsonNode questions = diagJson.get("questions");

        // 3. Submit answers: get Q1 (Variables, correct='A') right, get Q5 (Inheritance, correct='C') wrong
        List<QuizSubmissionRequest.QuestionAnswer> answers = new ArrayList<>();
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(0).get("id").asLong(), "A")); // Correct
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(1).get("id").asLong(), "C")); // Correct
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(2).get("id").asLong(), "C")); // Correct
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(3).get("id").asLong(), "C")); // Correct
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(4).get("id").asLong(), "A")); // Wrong (Inheritance)
        answers.add(new QuizSubmissionRequest.QuestionAnswer(questions.get(5).get("id").asLong(), "C")); // Correct

        QuizSubmissionRequest subReq = new QuizSubmissionRequest();
        subReq.setQuizId(diagQuizId);
        subReq.setAnswers(answers);

        mockMvc.perform(post("/api/quizzes/submit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalScore").value(5))
                .andExpect(jsonPath("$.totalQuestions").value(6))
                .andExpect(jsonPath("$.topicBreakdown.length()").value(6))
                .andExpect(jsonPath("$.topicBreakdown[0].statusLabel").value("MASTERED"))
                .andExpect(jsonPath("$.topicBreakdown[4].statusLabel").value("WEAK"));

        // 4. Verify course detail now shows diagnosticCompleted = true
        mockMvc.perform(get("/api/courses/" + courseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnosticCompleted").value(true));

        // 5. Verify Practice Mode includes instant feedback fields (correctOption & explanation)
        Long firstTopicId = questions.get(0).get("topicId").asLong();
        mockMvc.perform(get("/api/quizzes/practice/" + firstTopicId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizType").value("PRACTICE"))
                .andExpect(jsonPath("$.questions[0].correctOption").isNotEmpty());
    }
}
