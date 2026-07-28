package com.vimalkumar.careerportal.aggregator;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Manual trigger for the aggregator, mainly for local testing via Swagger UI
 * so you don't have to wait for the 6 AM scheduled run.
 * POST /api/admin/jobs/fetch-now
 */
@RestController
@RequestMapping("/api/admin/jobs")
public class JobAggregatorController {

    private final JobAggregatorService jobAggregatorService;
    private final CompanyAtsAggregatorService companyAtsAggregatorService;

    public JobAggregatorController(JobAggregatorService jobAggregatorService,
                                    CompanyAtsAggregatorService companyAtsAggregatorService) {
        this.jobAggregatorService = jobAggregatorService;
        this.companyAtsAggregatorService = companyAtsAggregatorService;
    }

    @PostMapping("/fetch-now")
    public ResponseEntity<Map<String, Object>> fetchNow() {
        int inserted = jobAggregatorService.fetchAndStoreLatestJobs();
        return ResponseEntity.ok(Map.of(
                "status", "completed",
                "newJobsInserted", inserted
        ));
    }

    /** Pulls fresh listings from all configured company Greenhouse/Lever boards. */
    @PostMapping("/fetch-company-ats-now")
    public ResponseEntity<Map<String, Object>> fetchCompanyAtsNow() {
        int inserted = companyAtsAggregatorService.fetchAndStoreLatestJobs();
        return ResponseEntity.ok(Map.of(
                "status", "completed",
                "newJobsInserted", inserted,
                "companiesConfigured", companyAtsAggregatorService.getConfiguredSources().size()
        ));
    }
}
