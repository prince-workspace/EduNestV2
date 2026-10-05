package com.edunest.repository;

import com.edunest.entity.Question;
import com.edunest.enums.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByTopicId(Long topicId);
    List<Question> findByTopicIdAndQuestionType(Long topicId, QuestionType questionType);
    List<Question> findByTopicCourseIdAndQuestionType(Long courseId, QuestionType questionType);
}
