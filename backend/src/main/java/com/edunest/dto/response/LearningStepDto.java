package com.edunest.dto.response;

import com.edunest.enums.MasteryLevel;

public class LearningStepDto {
    private int stepOrder;
    private Long topicId;
    private String topicTitle;
    private double masteryScore;
    private MasteryLevel masteryLevel;
    private String actionType; // READ_LESSON, PRACTICE, TAKE_QUIZ, REVISION, CHALLENGE
    private String recommendationText;
    private String priority;   // HIGH, MEDIUM, LOW, BONUS
    private String targetUrl;

    public LearningStepDto() {
    }

    public LearningStepDto(int stepOrder, Long topicId, String topicTitle,
                           double masteryScore, MasteryLevel masteryLevel,
                           String actionType, String recommendationText,
                           String priority, String targetUrl) {
        this.stepOrder = stepOrder;
        this.topicId = topicId;
        this.topicTitle = topicTitle;
        this.masteryScore = masteryScore;
        this.masteryLevel = masteryLevel;
        this.actionType = actionType;
        this.recommendationText = recommendationText;
        this.priority = priority;
        this.targetUrl = targetUrl;
    }

    public int getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(int stepOrder) {
        this.stepOrder = stepOrder;
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

    public double getMasteryScore() {
        return masteryScore;
    }

    public void setMasteryScore(double masteryScore) {
        this.masteryScore = masteryScore;
    }

    public MasteryLevel getMasteryLevel() {
        return masteryLevel;
    }

    public void setMasteryLevel(MasteryLevel masteryLevel) {
        this.masteryLevel = masteryLevel;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getRecommendationText() {
        return recommendationText;
    }

    public void setRecommendationText(String recommendationText) {
        this.recommendationText = recommendationText;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
}
