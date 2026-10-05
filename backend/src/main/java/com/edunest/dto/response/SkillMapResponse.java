package com.edunest.dto.response;

import java.util.List;

public class SkillMapResponse {
    private Long courseId;
    private String courseTitle;
    private double overallMastery;
    private int unlockedChallengesCount;
    private int completedChallengesCount;
    private List<SkillMapEntryDto> entries;

    public SkillMapResponse() {
    }

    public SkillMapResponse(Long courseId, String courseTitle, double overallMastery,
                            int unlockedChallengesCount, int completedChallengesCount,
                            List<SkillMapEntryDto> entries) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.overallMastery = overallMastery;
        this.unlockedChallengesCount = unlockedChallengesCount;
        this.completedChallengesCount = completedChallengesCount;
        this.entries = entries;
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

    public double getOverallMastery() {
        return overallMastery;
    }

    public void setOverallMastery(double overallMastery) {
        this.overallMastery = overallMastery;
    }

    public int getUnlockedChallengesCount() {
        return unlockedChallengesCount;
    }

    public void setUnlockedChallengesCount(int unlockedChallengesCount) {
        this.unlockedChallengesCount = unlockedChallengesCount;
    }

    public int getCompletedChallengesCount() {
        return completedChallengesCount;
    }

    public void setCompletedChallengesCount(int completedChallengesCount) {
        this.completedChallengesCount = completedChallengesCount;
    }

    public List<SkillMapEntryDto> getEntries() {
        return entries;
    }

    public void setEntries(List<SkillMapEntryDto> entries) {
        this.entries = entries;
    }
}
