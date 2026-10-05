package com.edunest.entity;

import com.edunest.enums.ChallengeType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "challenges")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String starterCode;

    /**
     * Semicolon-separated list of expected keywords/tokens for rule-based code verification
     * e.g. "final;int" or "extends;@Override"
     */
    @Column(columnDefinition = "TEXT")
    private String expectedKeywords;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeType challengeType = ChallengeType.CODING;

    @Column(nullable = false)
    private String difficulty = "MEDIUM";

    @Column(nullable = false)
    private int points = 50;

    @Column(nullable = false)
    private double unlockThreshold = 90.0;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Challenge() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
