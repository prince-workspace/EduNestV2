package com.edunest;

import com.edunest.dto.request.CreateCourseRequest;
import com.edunest.dto.request.CreateLessonRequest;
import com.edunest.dto.request.CreateTopicRequest;
import com.edunest.dto.request.LoginRequest;
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
public class CourseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String loginAndGetToken(String email, String password) throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail(email);
        req.setPassword(password);

        MvcResult res = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(res.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    void testCourseCatalogEnrollmentAndLessonFlow() throws Exception {
        // 1. Public course catalog returns seeded Java course
        MvcResult catalogRes = mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java Programming - From Fundamentals to OOP"))
                .andExpect(jsonPath("$[0].topicCount").value(6))
                .andReturn();

        Long javaCourseId = objectMapper.readTree(catalogRes.getResponse().getContentAsString())
                .get(0).get("id").asLong();

        // 2. Student logs in and enrolls in Java course
        String studentToken = loginAndGetToken("student@edunest.com", "password123");

        mockMvc.perform(post("/api/courses/" + javaCourseId + "/enroll")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());

        // 3. Verify course detail shows enrolled = true and 6 topics
        MvcResult detailRes = mockMvc.perform(get("/api/courses/" + javaCourseId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrolled").value(true))
                .andExpect(jsonPath("$.topics.length()").value(6))
                .andReturn();

        Long firstLessonId = objectMapper.readTree(detailRes.getResponse().getContentAsString())
                .get("topics").get(0).get("lessons").get(0).get("id").asLong();

        // 4. Verify lesson viewer endpoint returns content and nextLessonId
        mockMvc.perform(get("/api/lessons/" + firstLessonId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Primitive Data Types & Variables"))
                .andExpect(jsonPath("$.nextLessonId").isNotEmpty());

        // 5. Instructor creates a new course, topic, lesson, and publishes it
        String instructorToken = loginAndGetToken("instructor@edunest.com", "password123");

        CreateCourseRequest courseReq = new CreateCourseRequest();
        courseReq.setTitle("Spring Boot Microservices");
        courseReq.setDescription("Build REST APIs with Spring Boot");

        MvcResult newCourseRes = mockMvc.perform(post("/api/instructor/courses")
                        .header("Authorization", "Bearer " + instructorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").value(false))
                .andReturn();

        Long newCourseId = objectMapper.readTree(newCourseRes.getResponse().getContentAsString()).get("id").asLong();

        CreateTopicRequest topicReq = new CreateTopicRequest();
        topicReq.setTitle("REST Controllers");
        topicReq.setOrderIndex(1);

        MvcResult newTopicRes = mockMvc.perform(post("/api/instructor/courses/" + newCourseId + "/topics")
                        .header("Authorization", "Bearer " + instructorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(topicReq)))
                .andExpect(status().isOk())
                .andReturn();

        Long newTopicId = objectMapper.readTree(newTopicRes.getResponse().getContentAsString()).get("id").asLong();

        CreateLessonRequest lessonReq = new CreateLessonRequest();
        lessonReq.setTitle("Building @RestController");
        lessonReq.setContent("<p>REST endpoints</p>");
        lessonReq.setKeyPoints("Use @GetMapping;Use @PostMapping");
        lessonReq.setEstimatedMinutes(10);
        lessonReq.setOrderIndex(1);

        mockMvc.perform(post("/api/instructor/topics/" + newTopicId + "/lessons")
                        .header("Authorization", "Bearer " + instructorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lessonReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Building @RestController"));

        mockMvc.perform(put("/api/instructor/courses/" + newCourseId + "/publish")
                        .header("Authorization", "Bearer " + instructorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").value(true));
    }
}
