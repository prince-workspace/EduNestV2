package com.edunest.repository;

import com.edunest.entity.TopicMastery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicMasteryRepository extends JpaRepository<TopicMastery, Long> {
    Optional<TopicMastery> findByUserIdAndTopicId(Long userId, Long topicId);
    List<TopicMastery> findByUserIdAndTopicCourseId(Long userId, Long courseId);
    List<TopicMastery> findByUserId(Long userId);
    List<TopicMastery> findByTopicCourseId(Long courseId);
}
