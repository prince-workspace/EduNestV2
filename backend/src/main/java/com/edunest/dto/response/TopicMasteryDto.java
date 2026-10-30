package com.edunest.dto.response;

import com.edunest.enums.MasteryLevel;

public class TopicMasteryDto {
    private Long topicId;
    private String topicTitle;
    private int orderIndex;
    private Double diagnosticScore;
    private Double practiceScore;
    private Double lessonScore;
    private Double quizScore;
    private Double revisionScore;
    private int completedLessons;
    private int totalLessons;
    private int practiceAttempts;
    private int quizAttempts;
    private double masteryScore;
    private MasteryLevel masteryLevel;
    private Long primaryLessonId;

    public TopicMasteryDto() {
    }

    public TopicMasteryDto(Long topicId, String topicTitle, int orderIndex,
                           Double diagnosticScore, Double practiceScore, Double lessonScore,
                           Double quizScore, Double revisionScore,
                           int completedLessons, int totalLessons,
                           int practiceAttempts, int quizAttempts,
                           double masteryScore, MasteryLevel masteryLevel,
                           Long primaryLessonId) {
        this.topicId = topicId;
        this.topicTitle = topicTitle;
        this.orderIndex = orderIndex;
        this.diagnosticScore = diagnosticScore;
        this.practiceScore = practiceScore;
        this.lessonScore = lessonScore;
        this.quizScore = quizScore;
        this.revisionScore = revisionScore;
        this.completedLessons = completedLessons;
        this.totalLessons = totalLessons;
        this.practiceAttempts = practiceAttempts;
        this.quizAttempts = quizAttempts;
        this.masteryScore = masteryScore;
        this.masteryLevel = masteryLevel;
        this.primaryLessonId = primaryLessonId;
    }

    // Backwards-compatible constructor
    public TopicMasteryDto(Long topicId, String topicTitle, int orderIndex,
                           Double diagnosticScore, Double practiceScore,
                           Double quizScore, Double revisionScore,
                           double masteryScore, MasteryLevel masteryLevel,
                           Long primaryLessonId) {
        this(topicId, topicTitle, orderIndex, diagnosticScore, practiceScore, 0.0,
             quizScore, revisionScore, 0, 0, 0, 0, masteryScore, masteryLevel, primaryLessonId);
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

    public Double getLessonScore() {
        return lessonScore;
    }

    public void setLessonScore(Double lessonScore) {
        this.lessonScore = lessonScore;
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

    public int getCompletedLessons() {
        return completedLessons;
    }

    public void setCompletedLessons(int completedLessons) {
        this.completedLessons = completedLessons;
    }

    public int getTotalLessons() {
        return totalLessons;
    }

    public void setTotalLessons(int totalLessons) {
        this.totalLessons = totalLessons;
    }

    public int getPracticeAttempts() {
        return practiceAttempts;
    }

    public void setPracticeAttempts(int practiceAttempts) {
        this.practiceAttempts = practiceAttempts;
    }

    public int getQuizAttempts() {
        return quizAttempts;
    }

    public void setQuizAttempts(int quizAttempts) {
        this.quizAttempts = quizAttempts;
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
