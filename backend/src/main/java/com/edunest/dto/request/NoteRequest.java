package com.edunest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NoteRequest {

    @NotNull(message = "Lesson ID is required")
    private Long lessonId;

    @NotBlank(message = "Note content is required")
    private String content;

    public NoteRequest() {
    }

    public NoteRequest(Long lessonId, String content) {
        this.lessonId = lessonId;
        this.content = content;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
