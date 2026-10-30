package com.edunest.dto.response;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeProfileResponse {
    private Long courseId;
    private String courseTitle;
    private double overallCourseMastery;
    private double overallCourseCompletion;
    private int completedLessonsCount;
    private int totalLessonsCount;
    private List<Long> completedLessonIds = new ArrayList<>();
    private Double averagePracticeScore;
    private Double averageQuizScore;
    private Double averageLessonScore;
    private int weakCount;
    private int needsWorkCount;
    private int strongCount;
    private int masteredCount;
    private List<TopicMasteryDto> topics = new ArrayList<>();

    public KnowledgeProfileResponse() {
    }

    public KnowledgeProfileResponse(Long courseId, String courseTitle, double overallCourseMastery,
                                    double overallCourseCompletion,
                                    int completedLessonsCount, int totalLessonsCount,
                                    List<Long> completedLessonIds,
                                    Double averagePracticeScore, Double averageQuizScore, Double averageLessonScore,
                                    int weakCount, int needsWorkCount, int strongCount,
                                    int masteredCount, List<TopicMasteryDto> topics) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.overallCourseMastery = overallCourseMastery;
        this.overallCourseCompletion = overallCourseCompletion;
        this.completedLessonsCount = completedLessonsCount;
        this.totalLessonsCount = totalLessonsCount;
        this.completedLessonIds = completedLessonIds != null ? completedLessonIds : new ArrayList<>();
        this.averagePracticeScore = averagePracticeScore;
        this.averageQuizScore = averageQuizScore;
        this.averageLessonScore = averageLessonScore;
        this.weakCount = weakCount;
        this.needsWorkCount = needsWorkCount;
        this.strongCount = strongCount;
        this.masteredCount = masteredCount;
        this.topics = topics != null ? topics : new ArrayList<>();
    }

    // Backwards-compatible constructor
    public KnowledgeProfileResponse(Long courseId, String courseTitle, double overallCourseMastery,
                                    int weakCount, int needsWorkCount, int strongCount,
                                    int masteredCount, List<TopicMasteryDto> topics) {
        this(courseId, courseTitle, overallCourseMastery, 0.0, 0, 0,
             new ArrayList<>(), null, null, null,
             weakCount, needsWorkCount, strongCount, masteredCount, topics);
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

    public double getOverallCourseCompletion() {
        return overallCourseCompletion;
    }

    public void setOverallCourseCompletion(double overallCourseCompletion) {
        this.overallCourseCompletion = overallCourseCompletion;
    }

    public int getCompletedLessonsCount() {
        return completedLessonsCount;
    }

    public void setCompletedLessonsCount(int completedLessonsCount) {
        this.completedLessonsCount = completedLessonsCount;
    }

    public int getTotalLessonsCount() {
        return totalLessonsCount;
    }

    public void setTotalLessonsCount(int totalLessonsCount) {
        this.totalLessonsCount = totalLessonsCount;
    }

    public List<Long> getCompletedLessonIds() {
        return completedLessonIds;
    }

    public void setCompletedLessonIds(List<Long> completedLessonIds) {
        this.completedLessonIds = completedLessonIds;
    }

    public Double getAveragePracticeScore() {
        return averagePracticeScore;
    }

    public void setAveragePracticeScore(Double averagePracticeScore) {
        this.averagePracticeScore = averagePracticeScore;
    }

    public Double getAverageQuizScore() {
        return averageQuizScore;
    }

    public void setAverageQuizScore(Double averageQuizScore) {
        this.averageQuizScore = averageQuizScore;
    }

    public Double getAverageLessonScore() {
        return averageLessonScore;
    }

    public void setAverageLessonScore(Double averageLessonScore) {
        this.averageLessonScore = averageLessonScore;
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
