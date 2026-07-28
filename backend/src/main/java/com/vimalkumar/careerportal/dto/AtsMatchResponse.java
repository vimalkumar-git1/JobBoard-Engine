package com.vimalkumar.careerportal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AtsMatchResponse {
    private Double matchScorePercent;
    private List<String> matchedSkills;
    private List<String> missingSkills;
}
