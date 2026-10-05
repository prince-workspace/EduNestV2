package com.edunest.dto.response;

import com.edunest.enums.QuizType;

import java.util.List;

public class QuizQuestionsResponse {
    private Long quizId;
    private Long courseId;
    private Long topicId;
    private String title;
    private QuizType quizType;
    private Integer timeLimitMinutes;
    private List<QuestionDto> questions;

    public QuizQuestionsResponse() {
    }

    public QuizQuestionsResponse(Long quizId, Long courseId, Long topicId, String title,
                                 QuizType quizType, Integer timeLimitMinutes, List<QuestionDto> questions) {
        this.quizId = quizId;
        this.courseId = courseId;
        this.topicId = topicId;
        this.title = title;
        this.quizType = quizType;
        this.timeLimitMinutes = timeLimitMinutes;
        this.questions = questions;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public QuizType getQuizType() {
        return quizType;
    }

    public void setQuizType(QuizType quizType) {
        this.quizType = quizType;
    }

    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(Integer timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public List<QuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDto> questions) {
        this.questions = questions;
    }
}
