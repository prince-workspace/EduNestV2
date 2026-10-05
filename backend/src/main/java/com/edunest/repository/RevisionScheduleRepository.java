package com.edunest.repository;

import com.edunest.entity.RevisionSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RevisionScheduleRepository extends JpaRepository<RevisionSchedule, Long> {
    List<RevisionSchedule> findByUserIdAndCompletedFalseOrderByDueDateAsc(Long userId);
    List<RevisionSchedule> findByUserIdOrderByCompletedAscDueDateAsc(Long userId);
    Optional<RevisionSchedule> findFirstByUserIdAndTopicIdAndCompletedFalse(Long userId, Long topicId);
}
