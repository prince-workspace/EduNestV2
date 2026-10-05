package com.edunest.service;

import com.edunest.dto.response.KnowledgeProfileResponse;
import com.edunest.dto.response.LearningStepDto;
import com.edunest.dto.response.TopicMasteryDto;
import com.edunest.enums.MasteryLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RecommendationService {

    private final MasteryService masteryService;

    public RecommendationService(MasteryService masteryService) {
        this.masteryService = masteryService;
    }

    /**
     * Rule-Based Adaptive Learning Path Generator (PRD Section 7):
     * - WEAK (< 40%): Recommend Lesson + Practice + Quiz (Priority: HIGH)
     * - NEEDS_WORK (40-69%): Recommend Practice + Quiz (Priority: MEDIUM)
     * - STRONG (70-89%): Allow progression + light revision (Priority: LOW)
     * - MASTERED (>= 90%): Recommend Advanced Practical Challenge (Priority: BONUS)
     *
     * Orders weak/needs-work prerequisite topics first by syllabus order so the student
     * masters weak foundations before advancing.
     */
    @Transactional(readOnly = true)
    public List<LearningStepDto> generatePersonalizedPath(String studentEmail, Long courseId) {
        KnowledgeProfileResponse profile = masteryService.getKnowledgeProfile(studentEmail, courseId);

        // Sort topics so WEAK comes first, then NEEDS_WORK, then STRONG, then MASTERED
        // Within the same mastery bucket, preserve natural syllabus order (orderIndex ASC)
        List<TopicMasteryDto> sortedTopics = new ArrayList<>(profile.getTopics());
        sortedTopics.sort(Comparator
                .comparingInt((TopicMasteryDto t) -> bucketPriority(t.getMasteryLevel()))
                .thenComparingInt(TopicMasteryDto::getOrderIndex));

        List<LearningStepDto> steps = new ArrayList<>();
        int stepCounter = 1;

        for (TopicMasteryDto t : sortedTopics) {
            MasteryLevel level = t.getMasteryLevel();
            String lessonUrl = t.getPrimaryLessonId() != null
                    ? "/student/lesson.html?id=" + t.getPrimaryLessonId()
                    : "/student/course-detail.html?id=" + profile.getCourseId();
            String practiceUrl = "/student/practice.html?topicId=" + t.getTopicId();
            String quizUrl = "/student/quiz.html?topicId=" + t.getTopicId();

            switch (level) {
                case WEAK -> {
                    steps.add(new LearningStepDto(
                            stepCounter++, t.getTopicId(), t.getTopicTitle(),
                            t.getMasteryScore(), level, "READ_LESSON",
                            "Weak Topic Detected (" + t.getMasteryScore() + "%): Study structured lesson for " + t.getTopicTitle(),
                            "HIGH", lessonUrl
                    ));
                    steps.add(new LearningStepDto(
                            stepCounter++, t.getTopicId(), t.getTopicTitle(),
                            t.getMasteryScore(), level, "PRACTICE",
                            "Complete guided concept practice on " + t.getTopicTitle(),
                            "HIGH", practiceUrl
                    ));
                    steps.add(new LearningStepDto(
                            stepCounter++, t.getTopicId(), t.getTopicTitle(),
                            t.getMasteryScore(), level, "TAKE_QUIZ",
                            "Verify mastery improvement with the " + t.getTopicTitle() + " Topic Quiz",
                            "HIGH", quizUrl
                    ));
                }
                case NEEDS_WORK -> {
                    steps.add(new LearningStepDto(
                            stepCounter++, t.getTopicId(), t.getTopicTitle(),
                            t.getMasteryScore(), level, "PRACTICE",
                            "Needs Practice (" + t.getMasteryScore() + "%): Reinforce weak spots in " + t.getTopicTitle(),
                            "MEDIUM", practiceUrl
                    ));
                    steps.add(new LearningStepDto(
                            stepCounter++, t.getTopicId(), t.getTopicTitle(),
                            t.getMasteryScore(), level, "TAKE_QUIZ",
                            "Retake Topic Quiz to push " + t.getTopicTitle() + " above 70% mastery",
                            "MEDIUM", quizUrl
                    ));
                }
                case STRONG -> {
                    steps.add(new LearningStepDto(
                            stepCounter++, t.getTopicId(), t.getTopicTitle(),
                            t.getMasteryScore(), level, "REVISION",
                            "Strong (" + t.getMasteryScore() + "%): Ready to progress! Do a quick review of " + t.getTopicTitle() + " to reach 90%+ mastery",
                            "LOW", quizUrl
                    ));
                }
                case MASTERED -> {
                    steps.add(new LearningStepDto(
                            stepCounter++, t.getTopicId(), t.getTopicTitle(),
                            t.getMasteryScore(), level, "CHALLENGE",
                            "Mastered (" + t.getMasteryScore() + "%): Apply " + t.getTopicTitle() + " in a Practical Coding Challenge",
                            "BONUS", "/student/challenges.html?courseId=" + profile.getCourseId()
                    ));
                }
            }
        }

        return steps;
    }

    private int bucketPriority(MasteryLevel level) {
        return switch (level) {
            case WEAK -> 1;
            case NEEDS_WORK -> 2;
            case STRONG -> 3;
            case MASTERED -> 4;
        };
    }
}
