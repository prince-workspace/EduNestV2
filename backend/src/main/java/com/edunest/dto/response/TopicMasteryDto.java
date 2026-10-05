package com.edunest.dto.response;

import com.edunest.enums.MasteryLevel;

public class TopicMasteryDto {
    private Long topicId;
    private String topicTitle;
    private int orderIndex;
    private Double diagnosticScore;
    private Double practiceScore;
    private Double quizScore;
    private Double revisionScore;
    private double masteryScore;
    private MasteryLevel masteryLevel;
    private Long primaryLessonId;

    public TopicMasteryDto() {
    }

    public TopicMasteryDto(Long topicId, String topicTitle, int orderIndex,
                           Double diagnosticScore, Double practiceScore,
                           Double quizScore, Double revisionScore,
                           double masteryScore, MasteryLevel masteryLevel,
                           Long primaryLessonId) {
        this.topicId = topicId;
        this.topicTitle = topicTitle;
        this.orderIndex = orderIndex;
        this.diagnosticScore = diagnosticScore;
        this.practiceScore = practiceScore;
        this.quizScore = quizScore;
        this.revisionScore = revisionScore;
        this.masteryScore = masteryScore;
        this.masteryLevel = masteryLevel;
        this.primaryLessonId = primaryLessonId;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Double getDiagnosticScore() {
        return diagnosticScore;
    }

    public void setDiagnosticScore(Double diagnosticScore) {
        this.diagnosticScore = diagnosticScore;
    }

    public Double getPracticeScore() {
        return practiceScore;
    }

    public void setPracticeScore(Double practiceScore) {
        this.practiceScore = practiceScore;
    }

    public Double getQuizScore() {
        return quizScore;
    }

    public void setQuizScore(Double quizScore) {
        this.quizScore = quizScore;
    }

    public Double getRevisionScore() {
        return revisionScore;
    }

    public void setRevisionScore(Double revisionScore) {
        this.revisionScore = revisionScore;
    }

    public double getMasteryScore() {
        return masteryScore;
    }

    public void setMasteryScore(double masteryScore) {
        this.masteryScore = masteryScore;
    }

    public MasteryLevel getMasteryLevel() {
        return masteryLevel;
    }

    public void setMasteryLevel(MasteryLevel masteryLevel) {
        this.masteryLevel = masteryLevel;
    }

    public Long getPrimaryLessonId() {
        return primaryLessonId;
    }

    public void setPrimaryLessonId(Long primaryLessonId) {
        this.primaryLessonId = primaryLessonId;
    }
}
