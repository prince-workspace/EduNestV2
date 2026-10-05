package com.edunest.repository;

import com.edunest.entity.LearningSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningSessionRepository extends JpaRepository<LearningSession, Long> {
    List<LearningSession> findByUserIdOrderBySessionDateDesc(Long userId);
}
