package com.edunest.repository;

import com.edunest.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByTopicIdOrderByOrderIndexAsc(Long topicId);
    List<Lesson> findByTopicCourseIdOrderByTopicOrderIndexAscOrderIndexAsc(Long courseId);
}
