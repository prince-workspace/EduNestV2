package com.edunest.entity;

import com.edunest.enums.MasteryLevel;
import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "topic_mastery",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "topic_id"}))
public class TopicMastery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(name = "diagnostic_score")
    private Double diagnosticScore;

    @Column(name = "practice_score")
    private Double practiceScore;

    @Column(name = "quiz_score")
    private Double quizScore;

    @Column(name = "revision_score")
    private Double revisionScore;

    @Column(name = "mastery_score", nullable = false)
    private double masteryScore = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "mastery_level", nullable = false, length = 20)
    private MasteryLevel masteryLevel = MasteryLevel.WEAK;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public TopicMastery() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Double getDiagnosticScore() {
        return diagnosticScore;
    }

    public void setDiagnosticScore(Double diagnosticScore) {
        this.diagnosticScore = diagnosticScore;
    }

    public Double getPracticeScore() {
        return practiceScore;
    }

    public void setPracticeScore(Double practiceScore) {
        this.practiceScore = practiceScore;
    }

    public Double getQuizScore() {
        return quizScore;
    }

    public void setQuizScore(Double quizScore) {
        this.quizScore = quizScore;
    }

    public Double getRevisionScore() {
        return revisionScore;
    }

    public void setRevisionScore(Double revisionScore) {
        this.revisionScore = revisionScore;
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

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
