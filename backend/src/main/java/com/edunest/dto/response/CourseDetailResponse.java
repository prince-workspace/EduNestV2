package com.edunest.dto.response;

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

    public List<TopicResponse> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicResponse> topics) {
        this.topics = topics;
    }
}
