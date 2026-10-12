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
    private final UserRepository userRepository;
    private final RevisionScheduleRepository revisionRepository;

    public MasteryService(TopicMasteryRepository masteryRepository,
                          CourseRepository courseRepository,
                          TopicRepository topicRepository,
                          LessonRepository lessonRepository,
                          UserRepository userRepository,
                          RevisionScheduleRepository revisionRepository) {
        this.masteryRepository = masteryRepository;
        this.courseRepository = courseRepository;
        this.topicRepository = topicRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
        this.revisionRepository = revisionRepository;
    }

    /**
     * Updates a specific assessment component (DIAGNOSTIC=20%, PRACTICE=20%, QUIZ=40%, REVISION=20%)
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
            case "DIAGNOSTIC" -> tm.setDiagnosticScore(score);
            case "PRACTICE" -> tm.setPracticeScore(score);
            case "QUIZ", "CHAPTER_QUIZ" -> tm.setQuizScore(score);
            case "REVISION" -> tm.setRevisionScore(score);
        }

        double weightedSum = 0.0;
        double activeWeight = 0.0;

        if (tm.getPracticeScore() != null) {
            weightedSum += tm.getPracticeScore() * 0.25;
            activeWeight += 0.25;
        }
        if (tm.getQuizScore() != null) {
            weightedSum += tm.getQuizScore() * 0.50;
            activeWeight += 0.50;
        }
        if (tm.getRevisionScore() != null) {
            weightedSum += tm.getRevisionScore() * 0.25;
            activeWeight += 0.25;
        }

        double calculated = activeWeight > 0 ? (weightedSum / activeWeight) : 0.0;
        double rounded = Math.round(calculated * 10.0) / 10.0;

        tm.setMasteryScore(rounded);
        tm.setMasteryLevel(MasteryLevel.fromScore(rounded));

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

        List<TopicMasteryDto> topicDtos = new ArrayList<>();
        int weakCount = 0;
        int needsWorkCount = 0;
        int strongCount = 0;
        int masteredCount = 0;
        double totalScore = 0.0;

        for (Topic t : courseTopics) {
            TopicMastery tm = masteryByTopic.get(t.getId());
            double score = 0.0;
            if (tm != null) {
                double wSum = 0.0;
                double aWeight = 0.0;
                if (tm.getPracticeScore() != null) {
                    wSum += tm.getPracticeScore() * 0.25;
                    aWeight += 0.25;
                }
                if (tm.getQuizScore() != null) {
                    wSum += tm.getQuizScore() * 0.50;
                    aWeight += 0.50;
                }
                if (tm.getRevisionScore() != null) {
                    wSum += tm.getRevisionScore() * 0.25;
                    aWeight += 0.25;
                }
                if (aWeight > 0) {
                    score = Math.round((wSum / aWeight) * 10.0) / 10.0;
                } else {
                    score = tm.getMasteryScore();
                }
            }
            MasteryLevel level = MasteryLevel.fromScore(score);

            switch (level) {
                case WEAK -> weakCount++;
                case NEEDS_WORK -> needsWorkCount++;
                case STRONG -> strongCount++;
                case MASTERED -> masteredCount++;
            }
            totalScore += score;

            List<Lesson> lessons = lessonRepository.findByTopicIdOrderByOrderIndexAsc(t.getId());
            Long primaryLessonId = lessons.isEmpty() ? null : lessons.get(0).getId();

            topicDtos.add(new TopicMasteryDto(
                    t.getId(),
                    t.getTitle(),
                    t.getOrderIndex(),
                    tm != null ? tm.getDiagnosticScore() : null,
                    tm != null ? tm.getPracticeScore() : null,
                    tm != null ? tm.getQuizScore() : null,
                    tm != null ? tm.getRevisionScore() : null,
                    score,
                    level,
                    primaryLessonId
            ));
        }

        double overallAvg = courseTopics.isEmpty()
                ? 0.0
                : Math.round((totalScore / courseTopics.size()) * 10.0) / 10.0;

        return new KnowledgeProfileResponse(
                course.getId(),
                course.getTitle(),
                overallAvg,
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
                    return new TopicMasteryDto(
                            t.getId(),
                            t.getTitle(),
                            t.getOrderIndex(),
                            tm.getDiagnosticScore(),
                            tm.getPracticeScore(),
                            tm.getQuizScore(),
                            tm.getRevisionScore(),
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
