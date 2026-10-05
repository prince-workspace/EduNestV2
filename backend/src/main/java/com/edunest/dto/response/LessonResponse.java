package com.edunest.dto.response;

public class LessonResponse {
    private Long id;
    private Long courseId;
    private Long topicId;
    private String topicTitle;
    private String title;
    private String content;
    private String keyPoints;
    private int estimatedMinutes;
    private int orderIndex;
    private Long previousLessonId;
    private Long nextLessonId;

    public LessonResponse() {
    }

    public LessonResponse(Long id, Long courseId, Long topicId, String topicTitle,
                          String title, String content, String keyPoints,
                          int estimatedMinutes, int orderIndex) {
        this.id = id;
        this.courseId = courseId;
        this.topicId = topicId;
        this.topicTitle = topicTitle;
        this.title = title;
        this.content = content;
        this.keyPoints = keyPoints;
        this.estimatedMinutes = estimatedMinutes;
        this.orderIndex = orderIndex;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getKeyPoints() {
        return keyPoints;
    }

    public void setKeyPoints(String keyPoints) {
        this.keyPoints = keyPoints;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Long getPreviousLessonId() {
        return previousLessonId;
    }

    public void setPreviousLessonId(Long previousLessonId) {
        this.previousLessonId = previousLessonId;
    }

    public Long getNextLessonId() {
        return nextLessonId;
    }

    public void setNextLessonId(Long nextLessonId) {
        this.nextLessonId = nextLessonId;
    }
}
