package com.edunest.dto.response;

public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String instructorName;
    private boolean published;
    private int topicCount;
    private long enrollmentCount;

    public CourseResponse() {
    }

    public CourseResponse(Long id, String title, String description, String thumbnailUrl,
                          String instructorName, boolean published, int topicCount, long enrollmentCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.instructorName = instructorName;
        this.published = published;
        this.topicCount = topicCount;
        this.enrollmentCount = enrollmentCount;
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

    public int getTopicCount() {
        return topicCount;
    }

    public void setTopicCount(int topicCount) {
        this.topicCount = topicCount;
    }

    public long getEnrollmentCount() {
        return enrollmentCount;
    }

    public void setEnrollmentCount(long enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
    }
}
