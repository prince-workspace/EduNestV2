package com.edunest.controller;

import com.edunest.dto.request.ChallengeSubmissionRequest;
import com.edunest.dto.request.CreateChallengeRequest;
import com.edunest.dto.response.ChallengeDto;
import com.edunest.dto.response.SkillMapResponse;
import com.edunest.dto.response.SubmissionResponse;
import com.edunest.service.ChallengeService;
import com.edunest.service.SkillMapService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SkillMapChallengeController {

    private final SkillMapService skillMapService;
    private final ChallengeService challengeService;

    public SkillMapChallengeController(SkillMapService skillMapService,
                                       ChallengeService challengeService) {
        this.skillMapService = skillMapService;
        this.challengeService = challengeService;
    }

    @GetMapping("/students/me/courses/{courseId}/skill-map")
    public ResponseEntity<SkillMapResponse> getCourseSkillMap(@PathVariable Long courseId,
                                                              Authentication authentication) {
        return ResponseEntity.ok(skillMapService.buildSkillMap(authentication.getName(), courseId));
    }

    @GetMapping("/students/me/skill-map")
    public ResponseEntity<SkillMapResponse> getDefaultSkillMap(@RequestParam(required = false) Long courseId,
                                                               Authentication authentication) {
        return ResponseEntity.ok(skillMapService.buildSkillMap(authentication.getName(), courseId));
    }

    @GetMapping("/challenges/course/{courseId}")
    public ResponseEntity<List<ChallengeDto>> getChallengesForCourse(@PathVariable Long courseId,
                                                                     @RequestParam(defaultValue = "false") boolean onlyUnlocked,
                                                                     Authentication authentication) {
        if (onlyUnlocked) {
            return ResponseEntity.ok(challengeService.getUnlockedChallengesForCourse(authentication.getName(), courseId));
        }
        return ResponseEntity.ok(challengeService.getChallengesForCourse(authentication.getName(), courseId));
    }

    @GetMapping("/challenges/{challengeId}")
    public ResponseEntity<ChallengeDto> getChallengeById(@PathVariable Long challengeId,
                                                         Authentication authentication) {
        return ResponseEntity.ok(challengeService.getChallengeById(challengeId, authentication.getName()));
    }

    @PostMapping("/challenges/{challengeId}/submit")
    public ResponseEntity<SubmissionResponse> submitChallenge(@PathVariable Long challengeId,
                                                              @Valid @RequestBody ChallengeSubmissionRequest req,
                                                              Authentication authentication) {
        return ResponseEntity.ok(challengeService.submitChallenge(challengeId, req, authentication.getName()));
    }

    @GetMapping("/challenges/my-submissions")
    public ResponseEntity<List<SubmissionResponse>> getMySubmissions(Authentication authentication) {
        return ResponseEntity.ok(challengeService.getMySubmissions(authentication.getName()));
    }

    @PostMapping("/instructor/challenges")
    public ResponseEntity<ChallengeDto> createChallenge(@Valid @RequestBody CreateChallengeRequest req) {
        return ResponseEntity.ok(challengeService.createChallenge(req));
    }
}
