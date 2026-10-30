package com.edunest.controller;

import com.edunest.dto.response.LessonResponse;
import com.edunest.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lessons")
@CrossOrigin(origins = "*")
public class LessonController {

    private final CourseService courseService;

    public LessonController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonResponse> getLessonById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getLessonById(id));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> completeLesson(@PathVariable Long id, Authentication authentication) {
        courseService.completeLesson(id, authentication.getName());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "lessonId", id,
                "message", "Lesson marked as completed and mastery updated"
        ));
    }

    @GetMapping("/completed")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<Long>> getCompletedLessons(@RequestParam(required = false) Long courseId,
                                                          Authentication authentication) {
        return ResponseEntity.ok(courseService.getCompletedLessonIds(authentication.getName(), courseId));
    }
}
