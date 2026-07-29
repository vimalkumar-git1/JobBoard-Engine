package com.vimalkumar.careerportal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobSearchRequest {
    private String jobRole;       // Job title/role to search for
    private String location;      // Location filter
    private String workMode;      // Work mode filter (REMOTE, HYBRID, ONSITE)
}
