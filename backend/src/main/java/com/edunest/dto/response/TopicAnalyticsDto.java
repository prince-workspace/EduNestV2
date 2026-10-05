package com.edunest.dto.response;

public class TopicAnalyticsDto {
    private Long topicId;
    private String topicTitle;
    private int orderIndex;
    private double averageMasteryScore;
    private int studentsAssessed;
    private int weakStudentCount;
    private int masteredStudentCount;

    public TopicAnalyticsDto() {
    }

    public TopicAnalyticsDto(Long topicId, String topicTitle, int orderIndex,
                             double averageMasteryScore, int studentsAssessed,
                             int weakStudentCount, int masteredStudentCount) {
        this.topicId = topicId;
        this.topicTitle = topicTitle;
        this.orderIndex = orderIndex;
        this.averageMasteryScore = averageMasteryScore;
        this.studentsAssessed = studentsAssessed;
        this.weakStudentCount = weakStudentCount;
        this.masteredStudentCount = masteredStudentCount;
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

    public double getAverageMasteryScore() {
        return averageMasteryScore;
    }

    public void setAverageMasteryScore(double averageMasteryScore) {
        this.averageMasteryScore = averageMasteryScore;
    }

    public int getStudentsAssessed() {
        return studentsAssessed;
    }

    public void setStudentsAssessed(int studentsAssessed) {
        this.studentsAssessed = studentsAssessed;
    }

    public int getWeakStudentCount() {
        return weakStudentCount;
    }

    public void setWeakStudentCount(int weakStudentCount) {
        this.weakStudentCount = weakStudentCount;
    }

    public int getMasteredStudentCount() {
        return masteredStudentCount;
    }

    public void setMasteredStudentCount(int masteredStudentCount) {
        this.masteredStudentCount = masteredStudentCount;
    }
}
