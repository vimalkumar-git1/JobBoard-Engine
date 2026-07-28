package com.vimalkumar.careerportal.aggregator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobAggregatorScheduler {

    private final JobAggregatorService jobAggregatorService;
    private final CompanyAtsAggregatorService companyAtsAggregatorService;

    public JobAggregatorScheduler(JobAggregatorService jobAggregatorService,
                                   CompanyAtsAggregatorService companyAtsAggregatorService) {
        this.jobAggregatorService = jobAggregatorService;
        this.companyAtsAggregatorService = companyAtsAggregatorService;
    }

    /** Runs every day at 6:00 AM server time. Change the cron in application.properties if needed. */
    @Scheduled(cron = "${adzuna.fetch-cron:0 0 6 * * *}")
    public void runDailyFetch() {
        log.info("Starting scheduled Adzuna job fetch...");
        int inserted = jobAggregatorService.fetchAndStoreLatestJobs();
        log.info("Scheduled Adzuna job fetch finished — {} new jobs added", inserted);
    }

    /** Runs every day at 6:30 AM — pulls from configured company Greenhouse/Lever boards. */
    @Scheduled(cron = "${company-ats.fetch-cron:0 30 6 * * *}")
    public void runDailyCompanyAtsFetch() {
        log.info("Starting scheduled company ATS job fetch...");
        int inserted = companyAtsAggregatorService.fetchAndStoreLatestJobs();
        log.info("Scheduled company ATS job fetch finished — {} new jobs added", inserted);
    }
}
