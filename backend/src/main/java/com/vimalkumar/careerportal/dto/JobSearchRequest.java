package com.vimalkumar.careerportal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobSearchRequest {
    private String keyword;       // matched against title/description
    private String techStack;     // e.g. "Java,Spring Boot"
    private String location;
    private String workMode;      // REMOTE / HYBRID / ONSITE
    private Double minExperience;
    private Double maxExperience;
}
