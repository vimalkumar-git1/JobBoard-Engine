package com.vimalkumar.careerportal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationCreateRequest {

    @NotNull
    private Long jobId;

    private Long resumeVersionId;
    private String notes;
}
