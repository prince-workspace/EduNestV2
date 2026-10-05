package com.edunest.dto.response;

import java.time.LocalDateTime;

public class NoteDto {
    private Long id;
    private Long lessonId;
    private String lessonTitle;
    private String topicTitle;
    private String courseTitle;
    private String content;
    private LocalDateTime updatedAt;

    public NoteDto() {
    }

    public NoteDto(Long id, Long lessonId, String lessonTitle, String topicTitle,
                   String courseTitle, String content, LocalDateTime updatedAt) {
        this.id = id;
        this.lessonId = lessonId;
        this.lessonTitle = lessonTitle;
        this.topicTitle = topicTitle;
        this.courseTitle = courseTitle;
        this.content = content;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
