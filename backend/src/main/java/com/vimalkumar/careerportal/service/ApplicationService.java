package com.vimalkumar.careerportal.service;

import com.vimalkumar.careerportal.dto.ApplicationCreateRequest;
import com.vimalkumar.careerportal.dto.ApplicationDto;
import com.vimalkumar.careerportal.entity.Application;
import com.vimalkumar.careerportal.entity.Job;
import com.vimalkumar.careerportal.entity.ResumeVersion;
import com.vimalkumar.careerportal.entity.User;
import com.vimalkumar.careerportal.exception.ResourceNotFoundException;
import com.vimalkumar.careerportal.repository.ApplicationRepository;
import com.vimalkumar.careerportal.repository.JobRepository;
import com.vimalkumar.careerportal.repository.ResumeVersionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final ResumeVersionRepository resumeVersionRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                               JobRepository jobRepository,
                               ResumeVersionRepository resumeVersionRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.resumeVersionRepository = resumeVersionRepository;
    }

    public ApplicationDto create(User user, ApplicationCreateRequest request) {
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + request.getJobId()));

        Application application = new Application();
        application.setUser(user);
        application.setJob(job);
        application.setNotes(request.getNotes());

        if (request.getResumeVersionId() != null) {
            ResumeVersion version = resumeVersionRepository.findById(request.getResumeVersionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resume version not found"));
            application.setResumeVersion(version);
        }

        return ApplicationDto.fromEntity(applicationRepository.save(application));
    }

    public List<ApplicationDto> getForUser(Long userId) {
        return applicationRepository.findByUserIdOrderByUpdatedAtDesc(userId)
                .stream()
                .map(ApplicationDto::fromEntity)
                .toList();
    }

    public ApplicationDto updateStatus(Long applicationId, Long userId, String newStatus) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        if (!application.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Application not found: " + applicationId);
        }

        Application.Status status = Application.Status.valueOf(newStatus.toUpperCase());
        application.setStatus(status);

        if (status == Application.Status.APPLIED && application.getAppliedAt() == null) {
            application.setAppliedAt(LocalDateTime.now());
        }

        return ApplicationDto.fromEntity(applicationRepository.save(application));
    }
}
