package com.vimalkumar.careerportal.aggregator;

import com.vimalkumar.careerportal.dto.adzuna.AdzunaJobResult;
import com.vimalkumar.careerportal.dto.adzuna.AdzunaSearchResponse;
import com.vimalkumar.careerportal.entity.Job;
import com.vimalkumar.careerportal.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Service
public class JobAggregatorService {

    private final AdzunaClient adzunaClient;
    private final JobRepository jobRepository;

    // Comma-separated search terms pulled each run, e.g. "java developer,react developer,data analyst"
    @Value("${adzuna.search-terms:java developer,spring boot developer,full stack developer}")
    private String searchTermsCsv;

    @Value("${adzuna.locations:chennai,bangalore,hyderabad,remote}")
    private String locationsCsv;

    @Value("${adzuna.results-per-term:20}")
    private int resultsPerTerm;

    public JobAggregatorService(AdzunaClient adzunaClient, JobRepository jobRepository) {
        this.adzunaClient = adzunaClient;
        this.jobRepository = jobRepository;
    }

    /**
     * Pulls fresh listings from Adzuna for every configured (term x location) combo,
     * skips anything already stored (by source URL), and saves the rest.
     * Returns the number of NEW jobs inserted.
     */
    public int fetchAndStoreLatestJobs() {
        if (!adzunaClient.isConfigured()) {
            log.warn("Adzuna app-id/app-key not set in application.properties — skipping fetch. " +
                    "Sign up free at https://developer.adzuna.com/signup");
            return 0;
        }

        List<String> terms = splitCsv(searchTermsCsv);
        List<String> locations = splitCsv(locationsCsv);

        int inserted = 0;
        for (String term : terms) {
            for (String location : locations) {
                inserted += fetchOneCombo(term, location);
            }
        }
        log.info("Adzuna aggregator run complete — {} new jobs inserted", inserted);
        return inserted;
    }

    private int fetchOneCombo(String term, String location) {
        try {
            AdzunaSearchResponse response = adzunaClient.search(term, location, 1, resultsPerTerm);
            if (response == null || response.getResults() == null) {
                return 0;
            }

            int count = 0;
            for (AdzunaJobResult result : response.getResults()) {
                if (saveIfNew(result)) {
                    count++;
                }
            }
            return count;
        } catch (Exception ex) {
            // One bad combo shouldn't kill the whole run.
            log.error("Adzuna fetch failed for term='{}' location='{}': {}", term, location, ex.getMessage());
            return 0;
        }
    }

    private boolean saveIfNew(AdzunaJobResult result) {
        String sourceUrl = result.getRedirect_url();
        if (sourceUrl == null || sourceUrl.isBlank()) {
            return false; // can't dedup or link back without a URL, skip it
        }
        if (jobRepository.existsBySourceUrl(sourceUrl)) {
            return false; // already have it
        }

        Job job = new Job();
        job.setTitle(nullSafe(result.getTitle(), "Untitled role"));
        job.setCompanyName(result.getCompany() != null
                ? nullSafe(result.getCompany().getDisplay_name(), "Unknown company")
                : "Unknown company");
        job.setDescription(nullSafe(result.getDescription(), ""));
        job.setLocation(result.getLocation() != null ? result.getLocation().getDisplay_name() : null);
        job.setTechStack(result.getCategory() != null ? result.getCategory().getLabel() : null);
        job.setSalaryMin(result.getSalary_min());
        job.setSalaryMax(result.getSalary_max());
        job.setSourcePortal("ADZUNA");
        job.setSourceUrl(sourceUrl);
        job.setPostedAt(parseDateSafely(result.getCreated()));
        job.setIsActive(true);

        jobRepository.save(job);
        return true;
    }

    private LocalDate parseDateSafely(String created) {
        if (created == null || created.isBlank()) return LocalDate.now();
        try {
            // Adzuna returns ISO-8601 with time, e.g. 2026-07-20T10:15:00Z
            return LocalDateTime.parse(created.replace("Z", "")).toLocalDate();
        } catch (DateTimeParseException e) {
            return LocalDate.now();
        }
    }

    private String nullSafe(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private List<String> splitCsv(String csv) {
        return List.of(csv.split(",")).stream().map(String::trim).filter(s -> !s.isEmpty()).toList();
    }
}
