package com.edunest.repository;

import com.edunest.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUserIdOrderByAttemptedAtDesc(Long userId);
    List<QuizAttempt> findByUserIdAndQuizIdOrderByAttemptedAtDesc(Long userId, Long quizId);
    Optional<QuizAttempt> findFirstByUserIdAndQuizIdOrderByAttemptedAtDesc(Long userId, Long quizId);
}
