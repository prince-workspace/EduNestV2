package com.edunest.repository;

import com.edunest.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    List<LessonProgress> findByUserIdAndCompletedTrue(Long userId);
    List<LessonProgress> findByUserIdAndLessonTopicCourseIdAndCompletedTrue(Long userId, Long courseId);
    List<LessonProgress> findByUserIdAndLessonTopicIdAndCompletedTrue(Long userId, Long topicId);
    Optional<LessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);
    boolean existsByUserIdAndLessonIdAndCompletedTrue(Long userId, Long lessonId);
    long countByUserIdAndLessonTopicIdAndCompletedTrue(Long userId, Long topicId);
    long countByUserIdAndLessonTopicCourseIdAndCompletedTrue(Long userId, Long courseId);
}
