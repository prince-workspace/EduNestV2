package com.edunest.controller;

import com.edunest.dto.request.CreateCourseRequest;
import com.edunest.dto.request.CreateLessonRequest;
import com.edunest.dto.request.CreateQuestionRequest;
import com.edunest.dto.request.CreateTopicRequest;
import com.edunest.dto.response.CourseResponse;
import com.edunest.dto.response.LessonResponse;
import com.edunest.dto.response.QuestionDto;
import com.edunest.dto.response.TopicResponse;
import com.edunest.service.CourseService;
import com.edunest.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructor")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('INSTRUCTOR')")
public class InstructorCourseController {

    private final CourseService courseService;
    private final QuizService quizService;

    public InstructorCourseController(CourseService courseService, QuizService quizService) {
        this.courseService = courseService;
        this.quizService = quizService;
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getMyInstructorCourses(Authentication authentication) {
        return ResponseEntity.ok(courseService.getInstructorCourses(authentication.getName()));
    }

    @PostMapping("/courses")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CreateCourseRequest request,
                                                       Authentication authentication) {
        return ResponseEntity.ok(courseService.createCourse(request, authentication.getName()));
    }

    @PostMapping("/courses/{courseId}/topics")
    public ResponseEntity<TopicResponse> addTopic(@PathVariable Long courseId,
                                                  @Valid @RequestBody CreateTopicRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.ok(courseService.addTopic(courseId, request, authentication.getName()));
    }

    @PostMapping("/topics/{topicId}/lessons")
    public ResponseEntity<LessonResponse> addLesson(@PathVariable Long topicId,
                                                    @Valid @RequestBody CreateLessonRequest request,
                                                    Authentication authentication) {
        return ResponseEntity.ok(courseService.addLesson(topicId, request, authentication.getName()));
    }

    @PostMapping("/topics/{topicId}/questions")
    public ResponseEntity<QuestionDto> addQuestion(@PathVariable Long topicId,
                                                   @Valid @RequestBody CreateQuestionRequest request,
                                                   Authentication authentication) {
        return ResponseEntity.ok(quizService.addQuestionToTopic(topicId, request, authentication.getName()));
    }

    @PutMapping("/courses/{courseId}/publish")
    public ResponseEntity<CourseResponse> publishCourse(@PathVariable Long courseId,
                                                        Authentication authentication) {
        return ResponseEntity.ok(courseService.publishCourse(courseId, authentication.getName()));
    }
}
