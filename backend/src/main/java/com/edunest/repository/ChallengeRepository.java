package com.edunest.repository;

import com.edunest.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findByTopicCourseIdOrderByTopicOrderIndexAsc(Long courseId);
    List<Challenge> findByTopicId(Long topicId);
}
