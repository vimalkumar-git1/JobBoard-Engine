package com.vimalkumar.careerportal.aggregator;

import com.vimalkumar.careerportal.entity.Job;
import com.vimalkumar.careerportal.repository.JobRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CompanyAtsAggregatorService {

    private final AtsBoardClient atsBoardClient;
    private final JobRepository jobRepository;

    /**
     * Format per line: CompanyDisplayName|atsType|boardSlug
     * One entry per line, blank lines ignored. Add/remove companies here.
     * Only slugs that are CONFIRMED correct are enabled by default —
     * verify each new one by opening https://boards-api.greenhouse.io/v1/boards/{slug}/jobs
     * or https://api.lever.co/v0/postings/{slug} in a browser before adding it.
     */
    @Value("${company-ats.sources:" +
            "Razorpay|greenhouse|razorpaysoftwareprivatelimited," +
            "Postman|greenhouse|postman," +
            "CRED|lever|cred," +
            "Meesho|lever|meesho," +
            "Groww|greenhouse-eu|groww," +
            "InMobi|greenhouse|inmobi" +
            "}")
    private String sourcesConfig;

    private List<CompanyAtsSource> companySources = new ArrayList<>();

    public CompanyAtsAggregatorService(AtsBoardClient atsBoardClient, JobRepository jobRepository) {
        this.atsBoardClient = atsBoardClient;
        this.jobRepository = jobRepository;
    }

    @PostConstruct
    void parseConfiguredSources() {
        companySources = sourcesConfig.lines()
                .flatMap(line -> java.util.Arrays.stream(line.split(",")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(entry -> {
                    String[] parts = entry.split("\\|");
                    if (parts.length != 3) {
                        log.warn("Skipping malformed company-ats.sources entry: {}", entry);
                        return null;
                    }
                    return new CompanyAtsSource(parts[0].trim(), parts[1].trim(), parts[2].trim());
                })
                .filter(java.util.Objects::nonNull)
                .toList();
        log.info("Loaded {} company ATS sources: {}", companySources.size(),
                companySources.stream().map(CompanyAtsSource::companyDisplayName).toList());
    }

    /** Returns number of NEW jobs inserted across all configured companies. */
    public int fetchAndStoreLatestJobs() {
        int inserted = 0;
        for (CompanyAtsSource source : companySources) {
            List<AtsBoardClient.NormalizedJob> jobs = atsBoardClient.fetch(source);
            for (AtsBoardClient.NormalizedJob nj : jobs) {
                if (saveIfNew(nj)) {
                    inserted++;
                }
            }
        }
        log.info("Company ATS aggregator run complete — {} new jobs inserted", inserted);
        return inserted;
    }

    private boolean saveIfNew(AtsBoardClient.NormalizedJob nj) {
        if (nj.sourceUrl() == null || nj.sourceUrl().isBlank()) return false;
        if (jobRepository.existsBySourceUrl(nj.sourceUrl())) return false;

        Job job = new Job();
        job.setTitle(nj.title());
        job.setCompanyName(nj.company());
        job.setDescription(nj.description() == null || nj.description().isBlank() ? "" : nj.description());
        job.setLocation(nj.location());
        job.setSourcePortal(nj.company().toUpperCase() + "_ATS");
        job.setSourceUrl(nj.sourceUrl());
        job.setPostedAt(nj.postedAt());
        job.setIsActive(true);

        jobRepository.save(job);
        return true;
    }

    public List<CompanyAtsSource> getConfiguredSources() {
        return companySources;
    }
}
