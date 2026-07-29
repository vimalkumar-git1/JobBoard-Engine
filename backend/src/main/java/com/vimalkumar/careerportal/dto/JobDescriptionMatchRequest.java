package com.vimalkumar.careerportal.dto;

public class JobDescriptionMatchRequest {
    private String jobDescription;
    private String jobTitle;

    public JobDescriptionMatchRequest() {}

    public JobDescriptionMatchRequest(String jobDescription, String jobTitle) {
        this.jobDescription = jobDescription;
        this.jobTitle = jobTitle;
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
}
