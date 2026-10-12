package com.edunest.controller;

import com.edunest.dto.response.KnowledgeProfileResponse;
import com.edunest.dto.response.LearningStepDto;
import com.edunest.dto.response.TopicMasteryDto;
import com.edunest.service.MasteryService;
import com.edunest.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students/me")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('STUDENT')")
public class MasteryController {

    private final MasteryService masteryService;
    private final RecommendationService recommendationService;
    private final com.edunest.service.UserService userService;

    public MasteryController(MasteryService masteryService,
                             RecommendationService recommendationService,
                             com.edunest.service.UserService userService) {
        this.masteryService = masteryService;
        this.recommendationService = recommendationService;
        this.userService = userService;
    }

    @PutMapping("/goal")
    public ResponseEntity<com.edunest.dto.response.AuthResponse> updateGoal(
            @RequestBody java.util.Map<String, String> payload,
            Authentication authentication) {
        String goal = payload.getOrDefault("learningGoal", "");
        return ResponseEntity.ok(userService.updateLearningGoal(authentication.getName(), goal));
    }

    @GetMapping("/mastery")
    public ResponseEntity<List<TopicMasteryDto>> getAllMyMastery(Authentication authentication) {
        return ResponseEntity.ok(masteryService.getAllStudentMastery(authentication.getName()));
    }

    @GetMapping("/knowledge-profile")
    public ResponseEntity<KnowledgeProfileResponse> getDefaultKnowledgeProfile(
            @RequestParam(required = false) Long courseId,
            Authentication authentication) {
        return ResponseEntity.ok(masteryService.getKnowledgeProfile(authentication.getName(), courseId));
    }

    @GetMapping("/courses/{courseId}/knowledge-profile")
    public ResponseEntity<KnowledgeProfileResponse> getCourseKnowledgeProfile(
            @PathVariable Long courseId,
            Authentication authentication) {
        return ResponseEntity.ok(masteryService.getKnowledgeProfile(authentication.getName(), courseId));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<LearningStepDto>> getDefaultRecommendations(
            @RequestParam(required = false) Long courseId,
            Authentication authentication) {
        return ResponseEntity.ok(recommendationService.generatePersonalizedPath(authentication.getName(), courseId));
    }

    @GetMapping({"/courses/{courseId}/recommendations", "/courses/{courseId}/learning-path"})
    public ResponseEntity<List<LearningStepDto>> getCourseRecommendations(
            @PathVariable Long courseId,
            Authentication authentication) {
        return ResponseEntity.ok(recommendationService.generatePersonalizedPath(authentication.getName(), courseId));
    }
}
