package com.vimalkumar.careerportal.dto.adzuna;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdzunaSearchResponse {
    private Integer count;
    private List<AdzunaJobResult> results;
}
