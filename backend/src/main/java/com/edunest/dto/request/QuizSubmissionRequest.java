package com.edunest.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class QuizSubmissionRequest {

    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    @NotNull(message = "Answers list is required")
    private List<QuestionAnswer> answers;

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public List<QuestionAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuestionAnswer> answers) {
        this.answers = answers;
    }

    public static class QuestionAnswer {
        private Long questionId;
        private String selectedOption;

        public QuestionAnswer() {
        }

        public QuestionAnswer(Long questionId, String selectedOption) {
            this.questionId = questionId;
            this.selectedOption = selectedOption;
        }

        public Long getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
        }

        public String getSelectedOption() {
            return selectedOption;
        }

        public void setSelectedOption(String selectedOption) {
            this.selectedOption = selectedOption;
        }
    }
}
