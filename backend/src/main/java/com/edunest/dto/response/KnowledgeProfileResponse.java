package com.edunest.dto.response;

import java.util.List;

public class KnowledgeProfileResponse {
    private Long courseId;
    private String courseTitle;
    private double overallCourseMastery;
    private int weakCount;
    private int needsWorkCount;
    private int strongCount;
    private int masteredCount;
    private List<TopicMasteryDto> topics;

    public KnowledgeProfileResponse() {
    }

    public KnowledgeProfileResponse(Long courseId, String courseTitle, double overallCourseMastery,
                                    int weakCount, int needsWorkCount, int strongCount,
                                    int masteredCount, List<TopicMasteryDto> topics) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.overallCourseMastery = overallCourseMastery;
        this.weakCount = weakCount;
        this.needsWorkCount = needsWorkCount;
        this.strongCount = strongCount;
        this.masteredCount = masteredCount;
        this.topics = topics;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public double getOverallCourseMastery() {
        return overallCourseMastery;
    }

    public void setOverallCourseMastery(double overallCourseMastery) {
        this.overallCourseMastery = overallCourseMastery;
    }

    public int getWeakCount() {
        return weakCount;
    }

    public void setWeakCount(int weakCount) {
        this.weakCount = weakCount;
    }

    public int getNeedsWorkCount() {
        return needsWorkCount;
    }

    public void setNeedsWorkCount(int needsWorkCount) {
        this.needsWorkCount = needsWorkCount;
    }

    public int getStrongCount() {
        return strongCount;
    }

    public void setStrongCount(int strongCount) {
        this.strongCount = strongCount;
    }

    public int getMasteredCount() {
        return masteredCount;
    }

    public void setMasteredCount(int masteredCount) {
        this.masteredCount = masteredCount;
    }

    public List<TopicMasteryDto> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicMasteryDto> topics) {
        this.topics = topics;
    }
}
