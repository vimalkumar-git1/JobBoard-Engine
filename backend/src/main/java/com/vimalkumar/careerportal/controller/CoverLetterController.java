package com.vimalkumar.careerportal.controller;

import com.vimalkumar.careerportal.entity.Job;
import com.vimalkumar.careerportal.entity.User;
import com.vimalkumar.careerportal.exception.ResourceNotFoundException;
import com.vimalkumar.careerportal.repository.JobRepository;
import com.vimalkumar.careerportal.service.CoverLetterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cover-letters")
public class CoverLetterController {

    private final CoverLetterService coverLetterService;
    private final JobRepository jobRepository;

    public CoverLetterController(CoverLetterService coverLetterService, JobRepository jobRepository) {
        this.coverLetterService = coverLetterService;
        this.jobRepository = jobRepository;
    }

    @GetMapping("/generate")
    public ResponseEntity<Map<String, String>> generate(@AuthenticationPrincipal User user,
                                                          @RequestParam Long jobId,
                                                          @RequestParam(required = false) String matchedSkills) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));

        String skillsCsv = matchedSkills != null ? matchedSkills : job.getTechStack();

        String letter = coverLetterService.generate(user.getFullName(), job.getTitle(), job.getCompanyName(), skillsCsv);
        return ResponseEntity.ok(Map.of("coverLetter", letter));
    }
}
