package com.edunest.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"}))
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @CreationTimestamp
    @Column(name = "enrolled_at", updatable = false)
    private LocalDateTime enrolledAt;

    @Column(name = "diagnostic_completed", nullable = false)
    private boolean diagnosticCompleted = false;

    @Column(name = "completed", nullable = false)
    private boolean completed = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "final_score")
    private Double finalScore;

    @Column(name = "final_assessment_passed", nullable = false)
    private boolean finalAssessmentPassed = false;

    @Column(name = "final_attempt_count", nullable = false)
    private int finalAttemptCount = 0;

    public Enrollment() {
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public boolean isDiagnosticCompleted() {
        return diagnosticCompleted;
    }

    public void setDiagnosticCompleted(boolean diagnosticCompleted) {
        this.diagnosticCompleted = diagnosticCompleted;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    public boolean isFinalAssessmentPassed() {
        return finalAssessmentPassed;
    }

    public void setFinalAssessmentPassed(boolean finalAssessmentPassed) {
        this.finalAssessmentPassed = finalAssessmentPassed;
    }

    public int getFinalAttemptCount() {
        return finalAttemptCount;
    }

    public void setFinalAttemptCount(int finalAttemptCount) {
        this.finalAttemptCount = finalAttemptCount;
    }
}
