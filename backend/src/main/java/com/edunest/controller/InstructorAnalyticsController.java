package com.edunest.controller;

import com.edunest.dto.response.CourseAnalyticsResponse;
import com.edunest.service.InstructorAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class InstructorAnalyticsController {

    private final InstructorAnalyticsService analyticsService;

    public InstructorAnalyticsController(InstructorAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/instructor/courses/{courseId}/analytics")
    public ResponseEntity<CourseAnalyticsResponse> getCourseAnalytics(@PathVariable Long courseId) {
        return ResponseEntity.ok(analyticsService.getCourseAnalytics(courseId));
    }

    @GetMapping("/instructor/analytics")
    public ResponseEntity<CourseAnalyticsResponse> getDefaultCourseAnalytics(@RequestParam(required = false) Long courseId) {
        return ResponseEntity.ok(analyticsService.getCourseAnalytics(courseId));
    }

    @GetMapping("/admin/overview")
    public ResponseEntity<Map<String, Object>> getAdminOverview() {
        return ResponseEntity.ok(analyticsService.getAdminPlatformOverview());
    }
}
