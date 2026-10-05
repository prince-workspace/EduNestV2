package com.edunest.repository;

import com.edunest.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByUserIdOrderBySubmittedAtDesc(Long userId);
    List<Submission> findByUserIdAndChallengeIdOrderBySubmittedAtDesc(Long userId, Long challengeId);
}
