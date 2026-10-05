package com.edunest;

import com.edunest.dto.request.BookmarkRequest;
import com.edunest.dto.request.LoginRequest;
import com.edunest.dto.request.NoteRequest;
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
public class Phase7AnalyticsAndNotesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String login(String email, String password) throws Exception {
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
    void testNotesBookmarksInstructorAnalyticsAndAdminOverview() throws Exception {
        String studentToken = login("student@edunest.com", "password123");
        String instructorToken = login("instructor@edunest.com", "password123");
        String adminToken = login("admin@edunest.com", "password123");

        // 1. Get Course ID and first Lesson ID
        MvcResult catalogRes = mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andReturn();
        Long courseId = objectMapper.readTree(catalogRes.getResponse().getContentAsString()).get(0).get("id").asLong();

        MvcResult detailRes = mockMvc.perform(get("/api/courses/" + courseId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andReturn();
        Long lessonId = objectMapper.readTree(detailRes.getResponse().getContentAsString())
                .get("topics").get(0).get("lessons").get(0).get("id").asLong();

        // 2. Save and retrieve a Lesson Note
        NoteRequest noteReq = new NoteRequest(lessonId, "Remember: final keyword creates constants; narrowing cast (int) truncates decimals.");
        mockMvc.perform(post("/api/students/me/notes")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lessonId").value(lessonId))
                .andExpect(jsonPath("$.content").value("Remember: final keyword creates constants; narrowing cast (int) truncates decimals."));

        mockMvc.perform(get("/api/students/me/notes/lesson/" + lessonId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lessonId").value(lessonId));

        mockMvc.perform(get("/api/students/me/notes")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // 3. Create, list, and delete a Lesson Bookmark
        BookmarkRequest bmReq = new BookmarkRequest(lessonId, "Important Java primitives reference");
        MvcResult bmRes = mockMvc.perform(post("/api/students/me/bookmarks")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bmReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noteLabel").value("Important Java primitives reference"))
                .andReturn();

        Long bookmarkId = objectMapper.readTree(bmRes.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/students/me/bookmarks")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(delete("/api/students/me/bookmarks/" + bookmarkId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());

        // 4. Instructor fetches Course Analytics
        mockMvc.perform(get("/api/instructor/courses/" + courseId + "/analytics")
                        .header("Authorization", "Bearer " + instructorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value(courseId))
                .andExpect(jsonPath("$.topicBreakdown.length()").value(6));

        // 5. Admin fetches Platform Overview
        mockMvc.perform(get("/api/admin/overview")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCourses").isNumber())
                .andExpect(jsonPath("$.totalUsers").isNumber());
    }
}
