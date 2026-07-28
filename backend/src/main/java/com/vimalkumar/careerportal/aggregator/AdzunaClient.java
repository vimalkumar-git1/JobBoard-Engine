package com.vimalkumar.careerportal.aggregator;

import com.vimalkumar.careerportal.dto.adzuna.AdzunaSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Thin client around Adzuna's public Job Search API (https://developer.adzuna.com/).
 * Adzuna is a legitimate, ToS-compliant aggregator API — this is NOT scraping
 * Naukri/LinkedIn, it is a licensed third-party job feed.
 *
 * Sign up for free credentials at https://developer.adzuna.com/signup and
 * set them in application.properties as adzuna.app-id / adzuna.app-key.
 */
@Component
public class AdzunaClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${adzuna.app-id:}")
    private String appId;

    @Value("${adzuna.app-key:}")
    private String appKey;

    @Value("${adzuna.country:in}")
    private String country; // 'in' = India; Adzuna also supports gb, us, etc.

    public boolean isConfigured() {
        return appId != null && !appId.isBlank() && appKey != null && !appKey.isBlank();
    }

    /**
     * @param what     free-text keywords, e.g. "java spring boot"
     * @param where    location, e.g. "chennai"
     * @param page     1-based page number
     * @param pageSize results per page (Adzuna max is 50)
     */
    public AdzunaSearchResponse search(String what, String where, int page, int pageSize) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://api.adzuna.com/v1/api/jobs/" + country + "/search/" + page)
                .queryParam("app_id", appId)
                .queryParam("app_key", appKey)
                .queryParam("results_per_page", pageSize)
                .queryParamIfPresent("what", java.util.Optional.ofNullable(emptyToNull(what)))
                .queryParamIfPresent("where", java.util.Optional.ofNullable(emptyToNull(where)))
                .queryParam("content-type", "application/json")
                .build()
                .toUriString();

        return restTemplate.getForObject(url, AdzunaSearchResponse.class);
    }

    private String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
