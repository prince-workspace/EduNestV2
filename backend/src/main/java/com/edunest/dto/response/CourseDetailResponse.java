package com.edunest.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class CourseDetailResponse {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String instructorName;
    private boolean published;
    private boolean enrolled;
    private boolean diagnosticCompleted;
    private boolean courseCompleted;
    private LocalDateTime courseCompletedAt;
    private Double finalScore;
    private boolean finalAssessmentPassed;
    private boolean finalAssessmentUnlocked;
    private int completedLessonsCount;
    private int totalLessonsCount;
    private double finalAssessmentPassingScore = 70.0;
    private List<TopicResponse> topics;

    public CourseDetailResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public boolean isDiagnosticCompleted() {
        return diagnosticCompleted;
    }

    public void setDiagnosticCompleted(boolean diagnosticCompleted) {
        this.diagnosticCompleted = diagnosticCompleted;
    }

    public boolean isCourseCompleted() {
        return courseCompleted;
    }

    public void setCourseCompleted(boolean courseCompleted) {
        this.courseCompleted = courseCompleted;
    }

    public LocalDateTime getCourseCompletedAt() {
        return courseCompletedAt;
    }

    public void setCourseCompletedAt(LocalDateTime courseCompletedAt) {
        this.courseCompletedAt = courseCompletedAt;
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    public boolean isFinalAssessmentPassed() {
        return finalAssessmentPassed;
    }

    public void setFinalAssessmentPassed(boolean finalAssessmentPassed) {
        this.finalAssessmentPassed = finalAssessmentPassed;
    }

    public boolean isFinalAssessmentUnlocked() {
        return finalAssessmentUnlocked;
    }

    public void setFinalAssessmentUnlocked(boolean finalAssessmentUnlocked) {
        this.finalAssessmentUnlocked = finalAssessmentUnlocked;
    }

    public int getCompletedLessonsCount() {
        return completedLessonsCount;
    }

    public void setCompletedLessonsCount(int completedLessonsCount) {
        this.completedLessonsCount = completedLessonsCount;
    }

    public int getTotalLessonsCount() {
        return totalLessonsCount;
    }

    public void setTotalLessonsCount(int totalLessonsCount) {
        this.totalLessonsCount = totalLessonsCount;
    }

    public double getFinalAssessmentPassingScore() {
        return finalAssessmentPassingScore;
    }

    public void setFinalAssessmentPassingScore(double finalAssessmentPassingScore) {
        this.finalAssessmentPassingScore = finalAssessmentPassingScore;
    }

    public List<TopicResponse> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicResponse> topics) {
        this.topics = topics;
    }
}
