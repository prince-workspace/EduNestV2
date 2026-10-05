package com.edunest.dto.response;

import java.time.LocalDateTime;

public class SubmissionResponse {
    private Long submissionId;
    private Long challengeId;
    private String challengeTitle;
    private boolean passed;
    private Double score;
    private String feedback;
    private LocalDateTime submittedAt;

    public SubmissionResponse() {
    }

    public SubmissionResponse(Long submissionId, Long challengeId, String challengeTitle,
                              boolean passed, Double score, String feedback, LocalDateTime submittedAt) {
        this.submissionId = submissionId;
        this.challengeId = challengeId;
        this.challengeTitle = challengeTitle;
        this.passed = passed;
        this.score = score;
        this.feedback = feedback;
        this.submittedAt = submittedAt;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
