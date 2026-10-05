package com.edunest.repository;

import com.edunest.entity.QuizAnswerRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizAnswerRecordRepository extends JpaRepository<QuizAnswerRecord, Long> {
    List<QuizAnswerRecord> findByAttemptId(Long attemptId);
}
