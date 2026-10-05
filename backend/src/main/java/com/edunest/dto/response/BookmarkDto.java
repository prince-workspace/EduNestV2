package com.edunest.dto.response;

import java.time.LocalDateTime;

public class BookmarkDto {
    private Long id;
    private Long lessonId;
    private String lessonTitle;
    private String topicTitle;
    private Long courseId;
    private String courseTitle;
    private String noteLabel;
    private LocalDateTime createdAt;

    public BookmarkDto() {
    }

    public BookmarkDto(Long id, Long lessonId, String lessonTitle, String topicTitle,
                       Long courseId, String courseTitle, String noteLabel, LocalDateTime createdAt) {
        this.id = id;
        this.lessonId = lessonId;
        this.lessonTitle = lessonTitle;
        this.topicTitle = topicTitle;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.noteLabel = noteLabel;
        this.createdAt = createdAt;
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

    public String getNoteLabel() {
        return noteLabel;
    }

    public void setNoteLabel(String noteLabel) {
        this.noteLabel = noteLabel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
