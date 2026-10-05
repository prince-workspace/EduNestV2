package com.edunest.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ChallengeSubmissionRequest {

    @NotBlank(message = "Solution text is required")
    private String solutionText;

    public ChallengeSubmissionRequest() {
    }

    public ChallengeSubmissionRequest(String solutionText) {
        this.solutionText = solutionText;
    }

    public String getSolutionText() {
        return solutionText;
    }

    public void setSolutionText(String solutionText) {
        this.solutionText = solutionText;
    }
}
