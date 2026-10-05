package com.edunest.dto.response;

import java.util.List;

public class CourseAnalyticsResponse {
    private Long courseId;
    private String courseTitle;
    private long enrolledStudentsCount;
    private int activeStudentsCount;
    private double overallAverageMastery;
    private List<TopicAnalyticsDto> weakestTopics;
    private List<TopicAnalyticsDto> topicBreakdown;

    public CourseAnalyticsResponse() {
    }

    public CourseAnalyticsResponse(Long courseId, String courseTitle,
                                   long enrolledStudentsCount, int activeStudentsCount,
                                   double overallAverageMastery,
                                   List<TopicAnalyticsDto> weakestTopics,
                                   List<TopicAnalyticsDto> topicBreakdown) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.enrolledStudentsCount = enrolledStudentsCount;
        this.activeStudentsCount = activeStudentsCount;
        this.overallAverageMastery = overallAverageMastery;
        this.weakestTopics = weakestTopics;
        this.topicBreakdown = topicBreakdown;
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

    public long getEnrolledStudentsCount() {
        return enrolledStudentsCount;
    }

    public void setEnrolledStudentsCount(long enrolledStudentsCount) {
        this.enrolledStudentsCount = enrolledStudentsCount;
    }

    public int getActiveStudentsCount() {
        return activeStudentsCount;
    }

    public void setActiveStudentsCount(int activeStudentsCount) {
        this.activeStudentsCount = activeStudentsCount;
    }

    public double getOverallAverageMastery() {
        return overallAverageMastery;
    }

    public void setOverallAverageMastery(double overallAverageMastery) {
        this.overallAverageMastery = overallAverageMastery;
    }

    public List<TopicAnalyticsDto> getWeakestTopics() {
        return weakestTopics;
    }

    public void setWeakestTopics(List<TopicAnalyticsDto> weakestTopics) {
        this.weakestTopics = weakestTopics;
    }

    public List<TopicAnalyticsDto> getTopicBreakdown() {
        return topicBreakdown;
    }

    public void setTopicBreakdown(List<TopicAnalyticsDto> topicBreakdown) {
        this.topicBreakdown = topicBreakdown;
    }
}
