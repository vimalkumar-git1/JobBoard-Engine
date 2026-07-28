package com.vimalkumar.careerportal.controller;

import com.vimalkumar.careerportal.dto.AtsMatchResponse;
import com.vimalkumar.careerportal.dto.ResumeUploadResponse;
import com.vimalkumar.careerportal.dto.ResumeVersionResponse;
import com.vimalkumar.careerportal.entity.Job;
import com.vimalkumar.careerportal.entity.Resume;
import com.vimalkumar.careerportal.entity.ResumeVersion;
import com.vimalkumar.careerportal.entity.User;
import com.vimalkumar.careerportal.exception.ResourceNotFoundException;
import com.vimalkumar.careerportal.repository.JobRepository;
import com.vimalkumar.careerportal.repository.ResumeRepository;
import com.vimalkumar.careerportal.service.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeParserService resumeParserService;
    private final AtsMatchService atsMatchService;
    private final ResumeGeneratorService resumeGeneratorService;
    private final ResumeVersionService resumeVersionService;
    private final FileStorageService fileStorageService;
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final com.vimalkumar.careerportal.repository.ResumeVersionRepository resumeVersionRepository;

    public ResumeController(ResumeParserService resumeParserService,
                             AtsMatchService atsMatchService,
                             ResumeGeneratorService resumeGeneratorService,
                             ResumeVersionService resumeVersionService,
                             FileStorageService fileStorageService,
                             ResumeRepository resumeRepository,
                             JobRepository jobRepository,
                             com.vimalkumar.careerportal.repository.ResumeVersionRepository resumeVersionRepository) {
        this.resumeParserService = resumeParserService;
        this.atsMatchService = atsMatchService;
        this.resumeGeneratorService = resumeGeneratorService;
        this.resumeVersionService = resumeVersionService;
        this.fileStorageService = fileStorageService;
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.resumeVersionRepository = resumeVersionRepository;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeUploadResponse> upload(@AuthenticationPrincipal User user,
                                                         @RequestParam("file") MultipartFile file) throws IOException {
        String text = resumeParserService.extractText(file);
        List<String> skills = resumeParserService.extractSkills(text);

        String storedPath = fileStorageService.saveUploadedResume(user.getId(), file.getOriginalFilename(), file.getBytes());

        Resume resume = new Resume();
        resume.setUser(user);
        resume.setOriginalFilename(file.getOriginalFilename());
        resume.setFilePath(storedPath);
        resume.setParsedText(text);
        resume.setParsedSkills(String.join(",", skills));

        resume = resumeRepository.save(resume);

        return ResponseEntity.ok(new ResumeUploadResponse(resume.getId(), resume.getOriginalFilename(), skills));
    }

    /** Compare an already-uploaded resume against a specific job's tech stack. */
    @GetMapping("/{resumeId}/match")
    public ResponseEntity<AtsMatchResponse> match(@PathVariable Long resumeId,
                                                    @RequestParam Long jobId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found: " + resumeId));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));

        List<String> resumeSkills = resume.getParsedSkills() == null
                ? List.of()
                : List.of(resume.getParsedSkills().split(","));

        return ResponseEntity.ok(atsMatchService.computeMatch(resumeSkills, job.getTechStack()));
    }

    /**
     * Generates a tailored PDF for this job, saves it, and records a new
     * version-controlled ResumeVersion linked to it.
     */
    @PostMapping("/{resumeId}/versions")
    public ResponseEntity<ResumeVersionResponse> createVersion(@AuthenticationPrincipal User user,
                                                                 @PathVariable Long resumeId,
                                                                 @RequestParam Long jobId) throws IOException {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found: " + resumeId));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));

        List<String> resumeSkills = resume.getParsedSkills() == null
                ? List.of()
                : List.of(resume.getParsedSkills().split(","));

        var matchResult = atsMatchService.computeMatch(resumeSkills, job.getTechStack());

        String summary = "Candidate with hands-on experience in " + String.join(", ", matchResult.getMatchedSkills())
                + ", applying for the " + job.getTitle() + " role at " + job.getCompanyName() + ".";

        byte[] pdfBytes = resumeGeneratorService.generate(
                user.getFullName(), job.getTitle(), summary,
                matchResult.getMatchedSkills(), matchResult.getMissingSkills());

        String versionLabel = resumeVersionService.peekNextVersionLabel(resume.getId(), job.getTitle());
        String savedPath = fileStorageService.saveGeneratedResume(user.getId(), versionLabel, pdfBytes);

        ResumeVersion version = resumeVersionService.createVersion(
                resume, user, job.getTitle(), matchResult.getMatchedSkills(),
                matchResult.getMatchScorePercent(), savedPath);

        return ResponseEntity.ok(ResumeVersionResponse.fromEntity(version));
    }

    @GetMapping("/versions")
    public ResponseEntity<List<ResumeVersionResponse>> listVersions(@AuthenticationPrincipal User user) {
        List<ResumeVersionResponse> versions = resumeVersionService.getVersionsForUser(user.getId())
                .stream()
                .map(ResumeVersionResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(versions);
    }

    @GetMapping("/versions/{versionId}/download")
    public ResponseEntity<byte[]> download(@AuthenticationPrincipal User user,
                                            @PathVariable Long versionId) throws IOException {
        ResumeVersion version = resumeVersionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume version not found: " + versionId));

        if (!version.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Resume version not found: " + versionId);
        }

        byte[] pdfBytes = fileStorageService.read(version.getGeneratedFilePath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + version.getVersionLabel() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
