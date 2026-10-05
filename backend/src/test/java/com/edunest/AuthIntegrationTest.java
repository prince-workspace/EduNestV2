package com.edunest;

import com.edunest.dto.request.LoginRequest;
import com.edunest.dto.request.RegisterRequest;
import com.edunest.enums.Role;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterLoginAndProtectedProfileFlow() throws Exception {
        // 1. Register a new student
        RegisterRequest registerReq = new RegisterRequest();
        registerReq.setName("Prince Prajapati");
        registerReq.setEmail("prince@edunest.com");
        registerReq.setPassword("secret123");
        registerReq.setRole(Role.STUDENT);
        registerReq.setLearningGoal("Master Java & Spring Boot");

        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.learningGoal").value("Master Java & Spring Boot"))
                .andReturn();

        JsonNode regJson = objectMapper.readTree(regResult.getResponse().getContentAsString());
        String token = regJson.get("token").asText();

        // 2. Access protected /api/auth/me with JWT
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("prince@edunest.com"))
                .andExpect(jsonPath("$.name").value("Prince Prajapati"));

        // 3. Login with seeded demo account
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("student@edunest.com");
        loginReq.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }
}
