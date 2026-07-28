package com.vimalkumar.careerportal.dto.adzuna;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Maps one job entry from Adzuna's /v1/api/jobs/{country}/search response.
 * Only the fields we actually use are declared — Jackson ignores the rest.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdzunaJobResult {

    private String id;
    private String title;
    private String description;
    private String redirect_url;
    private String created;

    private AdzunaCompany company;
    private AdzunaLocation location;
    private AdzunaCategory category;

    private Double salary_min;
    private Double salary_max;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AdzunaCompany {
        private String display_name;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AdzunaLocation {
        private String display_name;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AdzunaCategory {
        private String label;
    }
}
