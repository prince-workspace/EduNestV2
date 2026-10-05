package com.edunest.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class LearningSessionRequest {

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int durationMinutes;

    private String activityType = "STUDY_SESSION";

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
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
}
