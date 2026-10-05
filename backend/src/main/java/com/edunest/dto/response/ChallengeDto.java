package com.edunest.dto.response;

import com.edunest.enums.ChallengeType;

public class ChallengeDto {
    private Long id;
    private Long topicId;
    private String topicTitle;
    private Long courseId;
    private String title;
    private String description;
    private String starterCode;
    private ChallengeType challengeType;
    private String difficulty;
    private int points;
    private double unlockThreshold;
    private double currentTopicMastery;
    private boolean unlocked;
    private boolean completed;
    private Double bestScore;
    private String lastFeedback;

    public ChallengeDto() {
    }

    public ChallengeDto(Long id, Long topicId, String topicTitle, Long courseId,
                        String title, String description, String starterCode,
                        ChallengeType challengeType, String difficulty, int points,
                        double unlockThreshold, double currentTopicMastery,
                        boolean unlocked, boolean completed, Double bestScore, String lastFeedback) {
        this.id = id;
        this.topicId = topicId;
        this.topicTitle = topicTitle;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.starterCode = starterCode;
        this.challengeType = challengeType;
        this.difficulty = difficulty;
        this.points = points;
        this.unlockThreshold = unlockThreshold;
        this.currentTopicMastery = currentTopicMastery;
        this.unlocked = unlocked;
        this.completed = completed;
        this.bestScore = bestScore;
        this.lastFeedback = lastFeedback;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
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

    public double getCurrentTopicMastery() {
        return currentTopicMastery;
    }

    public void setCurrentTopicMastery(double currentTopicMastery) {
        this.currentTopicMastery = currentTopicMastery;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Double getBestScore() {
        return bestScore;
    }

    public void setBestScore(Double bestScore) {
        this.bestScore = bestScore;
    }

    public String getLastFeedback() {
        return lastFeedback;
    }

    public void setLastFeedback(String lastFeedback) {
        this.lastFeedback = lastFeedback;
    }
}
