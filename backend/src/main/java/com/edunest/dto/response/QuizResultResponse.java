package com.edunest.dto.response;

import com.edunest.enums.QuizType;

import java.util.List;

public class QuizResultResponse {
    private Long attemptId;
    private Long courseId;
    private String quizTitle;
    private QuizType quizType;
    private int totalScore;
    private int totalQuestions;
    private double overallPercentage;
    private List<TopicScoreDto> topicBreakdown;
    private List<AnswerDetail> answerDetails;

    public QuizResultResponse() {
    }

    public QuizResultResponse(Long attemptId, Long courseId, String quizTitle, QuizType quizType,
                              int totalScore, int totalQuestions, double overallPercentage,
                              List<TopicScoreDto> topicBreakdown, List<AnswerDetail> answerDetails) {
        this.attemptId = attemptId;
        this.courseId = courseId;
        this.quizTitle = quizTitle;
        this.quizType = quizType;
        this.totalScore = totalScore;
        this.totalQuestions = totalQuestions;
        this.overallPercentage = overallPercentage;
        this.topicBreakdown = topicBreakdown;
        this.answerDetails = answerDetails;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public QuizType getQuizType() {
        return quizType;
    }

    public void setQuizType(QuizType quizType) {
        this.quizType = quizType;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public double getOverallPercentage() {
        return overallPercentage;
    }

    public void setOverallPercentage(double overallPercentage) {
        this.overallPercentage = overallPercentage;
    }

    public List<TopicScoreDto> getTopicBreakdown() {
        return topicBreakdown;
    }

    public void setTopicBreakdown(List<TopicScoreDto> topicBreakdown) {
        this.topicBreakdown = topicBreakdown;
    }

    public List<AnswerDetail> getAnswerDetails() {
        return answerDetails;
    }

    public void setAnswerDetails(List<AnswerDetail> answerDetails) {
        this.answerDetails = answerDetails;
    }

    public static class AnswerDetail {
        private Long questionId;
        private Long topicId;
        private String topicTitle;
        private String questionText;
        private String selectedOption;
        private String correctOption;
        private boolean correct;
        private String explanation;

        public AnswerDetail() {
        }

        public AnswerDetail(Long questionId, Long topicId, String topicTitle, String questionText,
                            String selectedOption, String correctOption, boolean correct, String explanation) {
            this.questionId = questionId;
            this.topicId = topicId;
            this.topicTitle = topicTitle;
            this.questionText = questionText;
            this.selectedOption = selectedOption;
            this.correctOption = correctOption;
            this.correct = correct;
            this.explanation = explanation;
        }

        public Long getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
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

        public String getQuestionText() {
            return questionText;
        }

        public void setQuestionText(String questionText) {
            this.questionText = questionText;
        }

        public String getSelectedOption() {
            return selectedOption;
        }

        public void setSelectedOption(String selectedOption) {
            this.selectedOption = selectedOption;
        }

        public String getCorrectOption() {
            return correctOption;
        }

        public void setCorrectOption(String correctOption) {
            this.correctOption = correctOption;
        }

        public boolean isCorrect() {
            return correct;
        }

        public void setCorrect(boolean correct) {
            this.correct = correct;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }
    }
}
