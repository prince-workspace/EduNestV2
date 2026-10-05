package com.edunest.dto.response;

public class TopicScoreDto {
    private Long topicId;
    private String topicTitle;
    private int correct;
    private int total;
    private double percentage;
    private String statusLabel; // WEAK, NEEDS_WORK, STRONG, MASTERED

    public TopicScoreDto() {
    }

    public TopicScoreDto(Long topicId, String topicTitle, int correct, int total,
                         double percentage, String statusLabel) {
        this.topicId = topicId;
        this.topicTitle = topicTitle;
        this.correct = correct;
        this.total = total;
        this.percentage = percentage;
        this.statusLabel = statusLabel;
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

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }
}
