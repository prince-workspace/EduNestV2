package com.edunest.service;

import com.edunest.dto.response.*;
import com.edunest.enums.MasteryLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SkillMapService {

    private final MasteryService masteryService;
    private final ChallengeService challengeService;

    public SkillMapService(MasteryService masteryService, ChallengeService challengeService) {
        this.masteryService = masteryService;
        this.challengeService = challengeService;
    }

    @Transactional(readOnly = true)
    public SkillMapResponse buildSkillMap(String studentEmail, Long courseId) {
        KnowledgeProfileResponse profile = masteryService.getKnowledgeProfile(studentEmail, courseId);
        List<ChallengeDto> challenges = challengeService.getChallengesForCourse(studentEmail, profile.getCourseId());

        Map<Long, ChallengeDto> challengeByTopic = challenges.stream()
                .collect(Collectors.toMap(
                        ChallengeDto::getTopicId,
                        c -> c,
                        (first, second) -> first
                ));

        List<SkillMapEntryDto> entries = new ArrayList<>();
        int unlockedCount = 0;
        int completedCount = 0;

        for (TopicMasteryDto t : profile.getTopics()) {
            ChallengeDto ch = challengeByTopic.get(t.getTopicId());
            Long challengeId = ch != null ? ch.getId() : null;
            String challengeTitle = ch != null ? ch.getTitle() : null;
            boolean unlocked = ch != null && ch.isUnlocked();
            boolean completed = ch != null && ch.isCompleted();

            if (unlocked) unlockedCount++;
            if (completed) completedCount++;

            String colorCode = toColorCode(t.getMasteryLevel());
            String actionUrl = resolveActionUrl(t, challengeId, unlocked, profile.getCourseId());

            entries.add(new SkillMapEntryDto(
                    t.getTopicId(),
                    t.getTopicTitle(),
                    t.getOrderIndex(),
                    t.getMasteryScore(),
                    t.getMasteryLevel(),
                    colorCode,
                    challengeId,
                    challengeTitle,
                    unlocked,
                    completed,
                    actionUrl
            ));
        }

        return new SkillMapResponse(
                profile.getCourseId(),
                profile.getCourseTitle(),
                profile.getOverallCourseMastery(),
                unlockedCount,
                completedCount,
                entries
        );
    }

    private String toColorCode(MasteryLevel level) {
        return switch (level) {
            case WEAK -> "RED";
            case NEEDS_WORK -> "YELLOW";
            case STRONG -> "GREEN";
            case MASTERED -> "GOLD";
        };
    }

    private String resolveActionUrl(TopicMasteryDto t, Long challengeId, boolean challengeUnlocked, Long courseId) {
        if (challengeUnlocked && challengeId != null) {
            return "/student/challenge-detail.html?id=" + challengeId;
        }
        return switch (t.getMasteryLevel()) {
            case WEAK -> t.getPrimaryLessonId() != null
                    ? "/student/lesson.html?id=" + t.getPrimaryLessonId()
                    : "/student/course-detail.html?id=" + courseId;
            case NEEDS_WORK -> "/student/practice.html?topicId=" + t.getTopicId();
            case STRONG, MASTERED -> "/student/quiz.html?topicId=" + t.getTopicId();
        };
    }
}
