package com.vimalkumar.careerportal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ResumeUploadResponse {
    private Long resumeId;
    private String originalFilename;
    private List<String> extractedSkills;
}
