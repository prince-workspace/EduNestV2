package com.edunest.repository;

import com.edunest.entity.Quiz;
import com.edunest.enums.QuizType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByCourseIdAndQuizType(Long courseId, QuizType quizType);
    Optional<Quiz> findByTopicIdAndQuizType(Long topicId, QuizType quizType);
    List<Quiz> findByCourseId(Long courseId);
}
