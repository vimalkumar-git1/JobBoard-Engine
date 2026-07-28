package com.vimalkumar.careerportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationStatusUpdateRequest {

    @NotBlank
    private String status; // SAVED, APPLIED, INTERVIEWING, OFFERED, REJECTED
}
