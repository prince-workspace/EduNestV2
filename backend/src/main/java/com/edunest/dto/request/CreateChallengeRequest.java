package com.edunest.dto.request;

import com.edunest.enums.ChallengeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateChallengeRequest {

    @NotNull(message = "Topic ID is required")
    private Long topicId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String starterCode;
    private String expectedKeywords;
    private ChallengeType challengeType = ChallengeType.CODING;
    private String difficulty = "MEDIUM";
    private int points = 50;
    private double unlockThreshold = 90.0;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStarterCode() {
        return starterCode;
    }

    public void setStarterCode(String starterCode) {
        this.starterCode = starterCode;
    }

    public String getExpectedKeywords() {
        return expectedKeywords;
    }

    public void setExpectedKeywords(String expectedKeywords) {
        this.expectedKeywords = expectedKeywords;
    }

    public ChallengeType getChallengeType() {
        return challengeType;
    }

    public void setChallengeType(ChallengeType challengeType) {
        this.challengeType = challengeType;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public double getUnlockThreshold() {
        return unlockThreshold;
    }

    public void setUnlockThreshold(double unlockThreshold) {
        this.unlockThreshold = unlockThreshold;
    }
}
