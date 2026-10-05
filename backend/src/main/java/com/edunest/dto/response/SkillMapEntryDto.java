package com.edunest.dto.response;

import com.edunest.enums.MasteryLevel;

public class SkillMapEntryDto {
    private Long topicId;
    private String topicTitle;
    private int orderIndex;
    private double masteryScore;
    private MasteryLevel masteryLevel;
    private String colorCode; // RED (<40), YELLOW (40-69), GREEN (70-89), GOLD (>=90)
    private Long challengeId;
    private String challengeTitle;
    private boolean challengeUnlocked;
    private boolean challengeCompleted;
    private String recommendedActionUrl;

    public SkillMapEntryDto() {
    }

    public SkillMapEntryDto(Long topicId, String topicTitle, int orderIndex,
                            double masteryScore, MasteryLevel masteryLevel, String colorCode,
                            Long challengeId, String challengeTitle,
                            boolean challengeUnlocked, boolean challengeCompleted,
                            String recommendedActionUrl) {
        this.topicId = topicId;
        this.topicTitle = topicTitle;
        this.orderIndex = orderIndex;
        this.masteryScore = masteryScore;
        this.masteryLevel = masteryLevel;
        this.colorCode = colorCode;
        this.challengeId = challengeId;
        this.challengeTitle = challengeTitle;
        this.challengeUnlocked = challengeUnlocked;
        this.challengeCompleted = challengeCompleted;
        this.recommendedActionUrl = recommendedActionUrl;
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

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
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

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }

    public boolean isChallengeUnlocked() {
        return challengeUnlocked;
    }

    public void setChallengeUnlocked(boolean challengeUnlocked) {
        this.challengeUnlocked = challengeUnlocked;
    }

    public boolean isChallengeCompleted() {
        return challengeCompleted;
    }

    public void setChallengeCompleted(boolean challengeCompleted) {
        this.challengeCompleted = challengeCompleted;
    }

    public String getRecommendedActionUrl() {
        return recommendedActionUrl;
    }

    public void setRecommendedActionUrl(String recommendedActionUrl) {
        this.recommendedActionUrl = recommendedActionUrl;
    }
}
