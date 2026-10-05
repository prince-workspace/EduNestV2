package com.edunest.dto.request;

import jakarta.validation.constraints.NotNull;

public class BookmarkRequest {

    @NotNull(message = "Lesson ID is required")
    private Long lessonId;

    private String noteLabel = "Bookmarked for review";

    public BookmarkRequest() {
    }

    public BookmarkRequest(Long lessonId, String noteLabel) {
        this.lessonId = lessonId;
        this.noteLabel = noteLabel;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public String getNoteLabel() {
        return noteLabel;
    }

    public void setNoteLabel(String noteLabel) {
        this.noteLabel = noteLabel;
    }
}
