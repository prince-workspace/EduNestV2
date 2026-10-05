package com.edunest.dto.response;

public class DailyPlanItemDto {
    private int order;
    private String taskType; // URGENT_REVISION, WEAK_TOPIC_LESSON, TARGETED_PRACTICE, TOPIC_QUIZ
    private String title;
    private String subtitle;
    private int estimatedMinutes;
    private String priorityBadge;
    private String targetUrl;

    public DailyPlanItemDto() {
    }

    public DailyPlanItemDto(int order, String taskType, String title, String subtitle,
                            int estimatedMinutes, String priorityBadge, String targetUrl) {
        this.order = order;
        this.taskType = taskType;
        this.title = title;
        this.subtitle = subtitle;
        this.estimatedMinutes = estimatedMinutes;
        this.priorityBadge = priorityBadge;
        this.targetUrl = targetUrl;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public String getPriorityBadge() {
        return priorityBadge;
    }

    public void setPriorityBadge(String priorityBadge) {
        this.priorityBadge = priorityBadge;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
}
