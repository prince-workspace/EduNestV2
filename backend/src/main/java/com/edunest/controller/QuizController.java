package com.edunest.controller;

import com.edunest.dto.request.QuizSubmissionRequest;
import com.edunest.dto.response.AttemptSummaryResponse;
import com.edunest.dto.response.QuizQuestionsResponse;
import com.edunest.dto.response.QuizResultResponse;
import com.edunest.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = "*")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/diagnostic/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<QuizQuestionsResponse> getDiagnosticQuiz(@PathVariable Long courseId) {
        return ResponseEntity.ok(quizService.getDiagnosticQuizQuestions(courseId));
    }

    @GetMapping("/practice/{topicId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<QuizQuestionsResponse> getPracticeQuestions(@PathVariable Long topicId) {
        return ResponseEntity.ok(quizService.getPracticeQuestions(topicId));
    }

    @GetMapping("/chapter/{topicId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<QuizQuestionsResponse> getChapterQuiz(@PathVariable Long topicId) {
        return ResponseEntity.ok(quizService.getChapterQuizQuestions(topicId));
    }

    @PostMapping("/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<QuizResultResponse> submitAttempt(@Valid @RequestBody QuizSubmissionRequest request,
                                                            Authentication authentication) {
        return ResponseEntity.ok(quizService.submitAttempt(request, authentication.getName()));
    }

    @GetMapping("/attempts/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<AttemptSummaryResponse>> getMyAttempts(Authentication authentication) {
        return ResponseEntity.ok(quizService.getMyAttempts(authentication.getName()));
    }

    @GetMapping("/attempts/{attemptId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<QuizResultResponse> getAttemptResult(@PathVariable Long attemptId,
                                                               Authentication authentication) {
        return ResponseEntity.ok(quizService.getAttemptResult(attemptId, authentication.getName()));
    }
}
