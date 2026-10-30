package com.edunest.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ChallengeSubmissionRequest {

    @NotBlank(message = "Solution text is required")
    private String solutionText;

    private String language = "JAVA";

    public ChallengeSubmissionRequest() {
    }

    public ChallengeSubmissionRequest(String solutionText) {
        this.solutionText = solutionText;
        this.language = "JAVA";
    }

    public ChallengeSubmissionRequest(String solutionText, String language) {
        this.solutionText = solutionText;
        this.language = language != null ? language : "JAVA";
    }

    public String getSolutionText() {
        return solutionText;
    }

    public void setSolutionText(String solutionText) {
        this.solutionText = solutionText;
    }

    public String getLanguage() {
        return language != null ? language : "JAVA";
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
