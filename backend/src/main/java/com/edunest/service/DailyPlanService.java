package com.edunest.service;

import com.edunest.dto.request.LearningSessionRequest;
import com.edunest.dto.response.DailyPlanItemDto;
import com.edunest.dto.response.LearningSessionResponse;
import com.edunest.dto.response.LearningStepDto;
import com.edunest.dto.response.RevisionDto;
import com.edunest.entity.Course;
import com.edunest.entity.LearningSession;
import com.edunest.entity.User;
import com.edunest.repository.CourseRepository;
import com.edunest.repository.LearningSessionRepository;
import com.edunest.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DailyPlanService {

    private final RevisionService revisionService;
    private final RecommendationService recommendationService;
    private final LearningSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public DailyPlanService(RevisionService revisionService,
                            RecommendationService recommendationService,
                            LearningSessionRepository sessionRepository,
                            UserRepository userRepository,
                            CourseRepository courseRepository) {
        this.revisionService = revisionService;
        this.recommendationService = recommendationService;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Generates a focused, manageable Daily Learning Plan (up to 5 tasks):
     * 1. Up to 2 Pending / Due Spaced Revisions
     * 2. Top Weak-Topic Lesson to study
     * 3. Top Concept Practice set
     * 4. Top Topic Assessment Quiz / Review
     */
    @Transactional(readOnly = true)
    public List<DailyPlanItemDto> generateDailyPlan(String studentEmail, Long courseId) {
        List<RevisionDto> pendingRevisions = revisionService.getStudentRevisions(studentEmail, true);
        List<LearningStepDto> recommendations = recommendationService.generatePersonalizedPath(studentEmail, courseId);

        List<DailyPlanItemDto> plan = new ArrayList<>();
        int order = 1;

        // 1. Add up to 2 pending revisions
        for (int i = 0; i < Math.min(2, pendingRevisions.size()); i++) {
            RevisionDto rev = pendingRevisions.get(i);
            plan.add(new DailyPlanItemDto(
                    order++,
                    "URGENT_REVISION",
                    "Spaced Revision: " + rev.getTopicTitle(),
                    "Stage " + rev.getIntervalStage() + " review - Current Mastery: " + rev.getCurrentMasteryScore() + "% (Due: " + rev.getDueDate() + ")",
                    10,
                    rev.isDueOrOverdue() ? "DUE TODAY" : "UPCOMING",
                    "/student/revision.html"
            ));
        }

        // 2. Add top READ_LESSON step
        recommendations.stream()
                .filter(s -> "READ_LESSON".equals(s.getActionType()))
                .findFirst()
                .ifPresent(lessonStep -> plan.add(new DailyPlanItemDto(
                        plan.size() + 1,
                        "WEAK_TOPIC_LESSON",
                        "Study Lesson: " + lessonStep.getTopicTitle(),
                        lessonStep.getRecommendationText(),
                        15,
                        "HIGH PRIORITY",
                        lessonStep.getTargetUrl()
                )));

        // 3. Add top PRACTICE step
        recommendations.stream()
                .filter(s -> "PRACTICE".equals(s.getActionType()))
                .findFirst()
                .ifPresent(practiceStep -> plan.add(new DailyPlanItemDto(
                        plan.size() + 1,
                        "TARGETED_PRACTICE",
                        "Concept Practice: " + practiceStep.getTopicTitle(),
                        practiceStep.getRecommendationText(),
                        10,
                        practiceStep.getPriority(),
                        practiceStep.getTargetUrl()
                )));

        // 4. Add top TAKE_QUIZ or CHALLENGE step if space remains (max 5 items)
        if (plan.size() < 5) {
            recommendations.stream()
                    .filter(s -> "TAKE_QUIZ".equals(s.getActionType()) || "CHALLENGE".equals(s.getActionType()))
                    .findFirst()
                    .ifPresent(quizStep -> plan.add(new DailyPlanItemDto(
                            plan.size() + 1,
                            "TOPIC_QUIZ",
                            "Verify Mastery: " + quizStep.getTopicTitle(),
                            quizStep.getRecommendationText(),
                            10,
                            quizStep.getPriority(),
                            quizStep.getTargetUrl()
                    )));
        }

        return plan;
    }

    @Transactional
    public LearningSessionResponse logLearningSession(LearningSessionRequest req, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findById(req.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        LearningSession session = new LearningSession();
        session.setUser(student);
        session.setCourse(course);
        session.setDurationMinutes(req.getDurationMinutes());
        session.setActivityType(req.getActivityType());

        LearningSession saved = sessionRepository.save(session);
        return new LearningSessionResponse(
                saved.getId(),
                course.getId(),
                course.getTitle(),
                saved.getDurationMinutes(),
                saved.getActivityType(),
                saved.getSessionDate()
        );
    }

    @Transactional(readOnly = true)
    public List<LearningSessionResponse> getMySessions(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return sessionRepository.findByUserIdOrderBySessionDateDesc(student.getId()).stream()
                .map(s -> new LearningSessionResponse(
                        s.getId(),
                        s.getCourse().getId(),
                        s.getCourse().getTitle(),
                        s.getDurationMinutes(),
                        s.getActivityType(),
                        s.getSessionDate()
                ))
                .toList();
    }
}
