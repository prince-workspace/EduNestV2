package com.edunest.dto.response;

import java.time.LocalDateTime;

public class LearningSessionResponse {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private int durationMinutes;
    private String activityType;
    private LocalDateTime sessionDate;

    public LearningSessionResponse() {
    }

    public LearningSessionResponse(Long id, Long courseId, String courseTitle,
                                   int durationMinutes, String activityType, LocalDateTime sessionDate) {
        this.id = id;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.durationMinutes = durationMinutes;
        this.activityType = activityType;
        this.sessionDate = sessionDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public LocalDateTime getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDateTime sessionDate) {
        this.sessionDate = sessionDate;
    }
}
