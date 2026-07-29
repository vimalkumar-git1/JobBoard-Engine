package com.vimalkumar.careerportal.controller;

import com.vimalkumar.careerportal.dto.JobDto;
import com.vimalkumar.careerportal.dto.JobSearchRequest;
import com.vimalkumar.careerportal.entity.Job;
import com.vimalkumar.careerportal.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /** Simplified search: GET /api/jobs/search?jobRole=java&location=chennai&workMode=REMOTE */
    @GetMapping("/search")
    public ResponseEntity<Page<JobDto>> search(JobSearchRequest request, Pageable pageable) {
        return ResponseEntity.ok(jobService.search(request, pageable));
    }

    /** Get job role suggestions for autocomplete. */
    @GetMapping("/suggestions/titles")
    public ResponseEntity<List<String>> getJobRoleSuggestions(
            @RequestParam(required = false, defaultValue = "") String q) {
        return ResponseEntity.ok(jobService.getJobRoleSuggestions(q));
    }

    /** Get location suggestions for autocomplete. */
    @GetMapping("/suggestions/locations")
    public ResponseEntity<List<String>> getLocationSuggestions(
            @RequestParam(required = false, defaultValue = "") String q) {
        return ResponseEntity.ok(jobService.getLocationSuggestions(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getById(id));
    }

    /** Admin/manual job entry — the scraper module (Phase 2) would call the service directly instead. */
    @PostMapping
    public ResponseEntity<JobDto> create(@RequestBody Job job) {
        return ResponseEntity.ok(jobService.create(job));
    }
}
