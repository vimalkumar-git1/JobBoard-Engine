package com.vimalkumar.careerportal.dto;

import java.util.List;

public class JobDescriptionMatchRequest {
    private String jobDescription;
    private String jobTitle;
    private List<String> selectedKeywords;

    public JobDescriptionMatchRequest() {}

    public JobDescriptionMatchRequest(String jobDescription, String jobTitle, List<String> selectedKeywords) {
        this.jobDescription = jobDescription;
        this.jobTitle = jobTitle;
        this.selectedKeywords = selectedKeywords;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public List<String> getSelectedKeywords() {
        return selectedKeywords;
    }

    public void setSelectedKeywords(List<String> selectedKeywords) {
        this.selectedKeywords = selectedKeywords;
    }
}
