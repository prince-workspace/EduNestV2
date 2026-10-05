package com.edunest.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RevisionDto {
    private Long id;
    private Long topicId;
    private String topicTitle;
    private Long courseId;
    private LocalDate dueDate;
    private int intervalStage;
    private boolean completed;
    private LocalDateTime completedAt;
    private boolean dueOrOverdue;
    private double currentMasteryScore;

    public RevisionDto() {
    }

    public RevisionDto(Long id, Long topicId, String topicTitle, Long courseId,
                       LocalDate dueDate, int intervalStage, boolean completed,
                       LocalDateTime completedAt, boolean dueOrOverdue, double currentMasteryScore) {
        this.id = id;
        this.topicId = topicId;
        this.topicTitle = topicTitle;
        this.courseId = courseId;
        this.dueDate = dueDate;
        this.intervalStage = intervalStage;
        this.completed = completed;
        this.completedAt = completedAt;
        this.dueOrOverdue = dueOrOverdue;
        this.currentMasteryScore = currentMasteryScore;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public int getIntervalStage() {
        return intervalStage;
    }

    public void setIntervalStage(int intervalStage) {
        this.intervalStage = intervalStage;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public boolean isDueOrOverdue() {
        return dueOrOverdue;
    }

    public void setDueOrOverdue(boolean dueOrOverdue) {
        this.dueOrOverdue = dueOrOverdue;
    }

    public double getCurrentMasteryScore() {
        return currentMasteryScore;
    }

    public void setCurrentMasteryScore(double currentMasteryScore) {
        this.currentMasteryScore = currentMasteryScore;
    }
}
