package com.edunest.controller;

import com.edunest.dto.response.CourseDetailResponse;
import com.edunest.dto.response.CourseResponse;
import com.edunest.service.CourseService;
import com.edunest.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    public CourseController(CourseService courseService, EnrollmentService enrollmentService) {
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllPublishedCourses() {
        return ResponseEntity.ok(courseService.getAllPublishedCourses());
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CourseResponse>> getMyCourses(Authentication authentication) {
        return ResponseEntity.ok(enrollmentService.getMyEnrolledCourses(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDetailResponse> getCourseDetail(@PathVariable Long id,
                                                                Authentication authentication) {
        String email = (authentication != null) ? authentication.getName() : null;
        return ResponseEntity.ok(courseService.getCourseDetail(id, email));
    }

    @PostMapping("/{id}/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, String>> enroll(@PathVariable Long id,
                                                      Authentication authentication) {
        enrollmentService.enroll(id, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Enrolled successfully"));
    }
}
