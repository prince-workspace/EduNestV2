package com.edunest.controller;

import com.edunest.dto.request.LearningSessionRequest;
import com.edunest.dto.response.DailyPlanItemDto;
import com.edunest.dto.response.LearningSessionResponse;
import com.edunest.dto.response.RevisionDto;
import com.edunest.dto.response.TopicMasteryDto;
import com.edunest.service.DailyPlanService;
import com.edunest.service.RevisionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students/me")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('STUDENT')")
public class RevisionController {

    private final RevisionService revisionService;
    private final DailyPlanService dailyPlanService;

    public RevisionController(RevisionService revisionService, DailyPlanService dailyPlanService) {
        this.revisionService = revisionService;
        this.dailyPlanService = dailyPlanService;
    }

    @GetMapping({"/revision-schedule", "/revisions"})
    public ResponseEntity<List<RevisionDto>> getRevisions(
            @RequestParam(defaultValue = "false") boolean onlyPending,
            Authentication authentication) {
        return ResponseEntity.ok(revisionService.getStudentRevisions(authentication.getName(), onlyPending));
    }

    @PostMapping({"/revision-schedule/{id}/complete", "/revisions/{id}/complete"})
    public ResponseEntity<TopicMasteryDto> completeRevision(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> body,
            Authentication authentication) {
        double score = 100.0;
        if (body != null && body.containsKey("score")) {
            score = Double.parseDouble(body.get("score").toString());
        }
        return ResponseEntity.ok(revisionService.completeRevision(id, score, authentication.getName()));
    }

    @GetMapping("/daily-plan")
    public ResponseEntity<List<DailyPlanItemDto>> getDailyPlan(
            @RequestParam(required = false) Long courseId,
            Authentication authentication) {
        return ResponseEntity.ok(dailyPlanService.generateDailyPlan(authentication.getName(), courseId));
    }

    @PostMapping("/sessions")
    public ResponseEntity<LearningSessionResponse> logSession(
            @Valid @RequestBody LearningSessionRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(dailyPlanService.logLearningSession(request, authentication.getName()));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<LearningSessionResponse>> getMySessions(Authentication authentication) {
        return ResponseEntity.ok(dailyPlanService.getMySessions(authentication.getName()));
    }
}
