package com.edunest.service;

import com.edunest.dto.response.KnowledgeProfileResponse;
import com.edunest.dto.response.TopicMasteryDto;
import com.edunest.entity.*;
import com.edunest.enums.MasteryLevel;
import com.edunest.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MasteryService {

    private final TopicMasteryRepository masteryRepository;
    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final UserRepository userRepository;
    private final RevisionScheduleRepository revisionRepository;

    public MasteryService(TopicMasteryRepository masteryRepository,
                          CourseRepository courseRepository,
                          TopicRepository topicRepository,
                          LessonRepository lessonRepository,
                          LessonProgressRepository lessonProgressRepository,
                          UserRepository userRepository,
                          RevisionScheduleRepository revisionRepository) {
        this.masteryRepository = masteryRepository;
        this.courseRepository = courseRepository;
        this.topicRepository = topicRepository;
        this.lessonRepository = lessonRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.userRepository = userRepository;
        this.revisionRepository = revisionRepository;
    }

    /**
     * Consistent, gradual retentive scoring mechanism.
     * Rewarded when performance improves.
     * When an attempt is lower (even 0%), never drops mastery to 0; guarantees retention with a maximum single-attempt decay.
     */
    public static double calculateGradualUpdate(Double currentScore, double newScore) {
        if (currentScore == null) {
            return Math.round(newScore * 10.0) / 10.0;
        }

        double updated;
        if (newScore >= currentScore) {
            // Reward improvement: 40% gain towards new score
            updated = currentScore + 0.40 * (newScore - currentScore);
        } else {
            // Retentive gradual decay: weighted blend with 85% guaranteed floor retention
            double blended = (currentScore * 0.80) + (newScore * 0.20);
            double floor = currentScore * 0.85;
            updated = Math.max(floor, blended);
        }

        return Math.round(updated * 10.0) / 10.0;
    }

    /**
     * Recomputes composite topic mastery and assigns MasteryLevel based on accumulated learning:
     * - Lesson completion (25% weight)
     * - Practice drills (25% weight)
     * - Topic Quiz / Final Assessment (35% weight)
     * - Diagnostic / Revision (15% weight)
     */
    public void recomputeMastery(TopicMastery tm) {
        double weightedSum = 0.0;
        double activeWeight = 0.0;

        if (tm.getLessonScore() != null && tm.getLessonScore() > 0) {
            weightedSum += tm.getLessonScore() * 0.25;
            activeWeight += 0.25;
        }
        if (tm.getPracticeScore() != null) {
            weightedSum += tm.getPracticeScore() * 0.25;
            activeWeight += 0.25;
        }
        if (tm.getQuizScore() != null) {
            weightedSum += tm.getQuizScore() * 0.35;
            activeWeight += 0.35;
        }
        if (tm.getDiagnosticScore() != null) {
            weightedSum += tm.getDiagnosticScore() * 0.15;
            activeWeight += 0.15;
        } else if (tm.getRevisionScore() != null) {
            weightedSum += tm.getRevisionScore() * 0.15;
            activeWeight += 0.15;
        }

        double calculated = activeWeight > 0 ? (weightedSum / activeWeight) : 0.0;
        double rounded = Math.round(calculated * 10.0) / 10.0;

        tm.setMasteryScore(rounded);
        tm.setMasteryLevel(MasteryLevel.fromScore(rounded));
    }

    /**
     * Updates topic mastery when a student completes a lesson in that topic.
     */
    @Transactional
    public TopicMastery updateMasteryFromLessonCompletion(Long userId, Long topicId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        TopicMastery tm = masteryRepository.findByUserIdAndTopicId(userId, topicId)
                .orElseGet(() -> {
                    TopicMastery created = new TopicMastery();
                    created.setUser(user);
                    created.setTopic(topic);
                    return created;
                });

        long totalLessons = lessonRepository.countByTopicId(topicId);
        long completedLessons = lessonProgressRepository.countByUserIdAndLessonTopicIdAndCompletedTrue(userId, topicId);

        double lessonPct = totalLessons > 0 ? ((double) completedLessons / totalLessons) * 100.0 : 0.0;
        tm.setLessonScore(Math.round(lessonPct * 10.0) / 10.0);

        recomputeMastery(tm);
        return masteryRepository.save(tm);
    }

    /**
     * Updates a specific assessment component using gradual retentive scoring
     * and recalculates topic mastery score and MasteryLevel.
     * Automatically schedules a spaced revision task if topic is WEAK or NEEDS_WORK.
     */
    @Transactional
    public TopicMastery updateMasteryFromAssessment(Long userId, Long topicId,
                                                    double score, String component) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        TopicMastery tm = masteryRepository.findByUserIdAndTopicId(userId, topicId)
                .orElseGet(() -> {
                    TopicMastery created = new TopicMastery();
                    created.setUser(user);
                    created.setTopic(topic);
                    return created;
                });

        switch (component.toUpperCase()) {
            case "DIAGNOSTIC" -> tm.setDiagnosticScore(calculateGradualUpdate(tm.getDiagnosticScore(), score));
            case "PRACTICE" -> {
                tm.setPracticeScore(calculateGradualUpdate(tm.getPracticeScore(), score));
                tm.setPracticeAttempts(tm.getPracticeAttempts() + 1);
            }
            case "QUIZ", "CHAPTER_QUIZ", "FINAL_ASSESSMENT" -> {
                tm.setQuizScore(calculateGradualUpdate(tm.getQuizScore(), score));
                tm.setQuizAttempts(tm.getQuizAttempts() + 1);
            }
            case "REVISION" -> tm.setRevisionScore(calculateGradualUpdate(tm.getRevisionScore(), score));
        }

        recomputeMastery(tm);
        TopicMastery saved = masteryRepository.save(tm);

        // Automatically schedule spaced revision if WEAK or NEEDS_WORK (unless triggered by REVISION itself)
        if (!"REVISION".equalsIgnoreCase(component) &&
            (saved.getMasteryLevel() == MasteryLevel.WEAK || saved.getMasteryLevel() == MasteryLevel.NEEDS_WORK)) {
            if (revisionRepository.findFirstByUserIdAndTopicIdAndCompletedFalse(userId, topicId).isEmpty()) {
                RevisionSchedule rev = new RevisionSchedule();
                rev.setUser(user);
                rev.setTopic(topic);
                rev.setIntervalStage(1);
                // WEAK topics due immediately (today), NEEDS_WORK due in +1 day
                rev.setDueDate(saved.getMasteryLevel() == MasteryLevel.WEAK
                        ? java.time.LocalDate.now()
                        : java.time.LocalDate.now().plusDays(1));
                rev.setCompleted(false);
                revisionRepository.save(rev);
            }
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public KnowledgeProfileResponse getKnowledgeProfile(String studentEmail, Long courseId) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = resolveCourse(courseId);
        List<Topic> courseTopics = topicRepository.findByCourseIdOrderByOrderIndexAsc(course.getId());

        Map<Long, TopicMastery> masteryByTopic = new HashMap<>();
        masteryRepository.findByUserIdAndTopicCourseId(student.getId(), course.getId())
                .forEach(tm -> masteryByTopic.put(tm.getTopic().getId(), tm));

        // Fetch completed lesson progress in this course
        List<LessonProgress> completedProgress =
                lessonProgressRepository.findByUserIdAndLessonTopicCourseIdAndCompletedTrue(student.getId(), course.getId());
        List<Long> completedLessonIds = completedProgress.stream()
                .map(lp -> lp.getLesson().getId())
                .toList();

        int completedLessonsCount = completedLessonIds.size();
        int totalLessonsCount = (int) lessonRepository.countByTopicCourseId(course.getId());

        List<TopicMasteryDto> topicDtos = new ArrayList<>();
        int weakCount = 0;
        int needsWorkCount = 0;
        int strongCount = 0;
        int masteredCount = 0;
        double totalMasteryScore = 0.0;

        double sumPractice = 0.0;
        int practiceCount = 0;
        double sumQuiz = 0.0;
        int quizCount = 0;
        double sumLesson = 0.0;

        for (Topic t : courseTopics) {
            TopicMastery tm = masteryByTopic.get(t.getId());
            double score = tm != null ? tm.getMasteryScore() : 0.0;
            MasteryLevel level = tm != null ? tm.getMasteryLevel() : MasteryLevel.WEAK;

            switch (level) {
                case WEAK -> weakCount++;
                case NEEDS_WORK -> needsWorkCount++;
                case STRONG -> strongCount++;
                case MASTERED -> masteredCount++;
            }
            totalMasteryScore += score;

            List<Lesson> lessons = lessonRepository.findByTopicIdOrderByOrderIndexAsc(t.getId());
            Long primaryLessonId = lessons.isEmpty() ? null : lessons.get(0).getId();
            int totalTopicLessons = lessons.size();
            int completedTopicLessons = (int) completedProgress.stream()
                    .filter(lp -> lp.getLesson().getTopic().getId().equals(t.getId()))
                    .count();

            double topicLessonScore = tm != null && tm.getLessonScore() != null
                    ? tm.getLessonScore()
                    : (totalTopicLessons > 0 ? ((double) completedTopicLessons / totalTopicLessons) * 100.0 : 0.0);
            sumLesson += topicLessonScore;

            if (tm != null && tm.getPracticeScore() != null) {
                sumPractice += tm.getPracticeScore();
                practiceCount++;
            }
            if (tm != null && tm.getQuizScore() != null) {
                sumQuiz += tm.getQuizScore();
                quizCount++;
            }

            topicDtos.add(new TopicMasteryDto(
                    t.getId(),
                    t.getTitle(),
                    t.getOrderIndex(),
                    tm != null ? tm.getDiagnosticScore() : null,
                    tm != null ? tm.getPracticeScore() : null,
                    Math.round(topicLessonScore * 10.0) / 10.0,
                    tm != null ? tm.getQuizScore() : null,
                    tm != null ? tm.getRevisionScore() : null,
                    completedTopicLessons,
                    totalTopicLessons,
                    tm != null ? tm.getPracticeAttempts() : 0,
                    tm != null ? tm.getQuizAttempts() : 0,
                    score,
                    level,
                    primaryLessonId
            ));
        }

        double overallMasteryAvg = courseTopics.isEmpty()
                ? 0.0
                : Math.round((totalMasteryScore / courseTopics.size()) * 10.0) / 10.0;

        double courseCompletionPct = totalLessonsCount > 0
                ? Math.round(((double) completedLessonsCount / totalLessonsCount) * 1000.0) / 10.0
                : 0.0;

        Double avgPractice = practiceCount > 0 ? Math.round((sumPractice / practiceCount) * 10.0) / 10.0 : null;
        Double avgQuiz = quizCount > 0 ? Math.round((sumQuiz / quizCount) * 10.0) / 10.0 : null;
        Double avgLesson = !courseTopics.isEmpty() ? Math.round((sumLesson / courseTopics.size()) * 10.0) / 10.0 : 0.0;

        return new KnowledgeProfileResponse(
                course.getId(),
                course.getTitle(),
                overallMasteryAvg,
                courseCompletionPct,
                completedLessonsCount,
                totalLessonsCount,
                completedLessonIds,
                avgPractice,
                avgQuiz,
                avgLesson,
                weakCount,
                needsWorkCount,
                strongCount,
                masteredCount,
                topicDtos
        );
    }

    @Transactional(readOnly = true)
    public List<TopicMasteryDto> getAllStudentMastery(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return masteryRepository.findByUserId(student.getId()).stream()
                .map(tm -> {
                    Topic t = tm.getTopic();
                    List<Lesson> lessons = lessonRepository.findByTopicIdOrderByOrderIndexAsc(t.getId());
                    Long primaryLessonId = lessons.isEmpty() ? null : lessons.get(0).getId();
                    long completed = lessonProgressRepository.countByUserIdAndLessonTopicIdAndCompletedTrue(student.getId(), t.getId());

                    return new TopicMasteryDto(
                            t.getId(),
                            t.getTitle(),
                            t.getOrderIndex(),
                            tm.getDiagnosticScore(),
                            tm.getPracticeScore(),
                            tm.getLessonScore(),
                            tm.getQuizScore(),
                            tm.getRevisionScore(),
                            (int) completed,
                            lessons.size(),
                            tm.getPracticeAttempts(),
                            tm.getQuizAttempts(),
                            tm.getMasteryScore(),
                            tm.getMasteryLevel(),
                            primaryLessonId
                    );
                })
                .toList();
    }

    private Course resolveCourse(Long courseId) {
        if (courseId != null) {
            return courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
        }
        return courseRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No courses available"));
    }
}
