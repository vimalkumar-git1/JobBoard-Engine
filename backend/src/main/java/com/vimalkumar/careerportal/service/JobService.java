package com.vimalkumar.careerportal.service;

import com.vimalkumar.careerportal.dto.JobDto;
import com.vimalkumar.careerportal.dto.JobSearchRequest;
import com.vimalkumar.careerportal.entity.Job;
import com.vimalkumar.careerportal.exception.ResourceNotFoundException;
import com.vimalkumar.careerportal.repository.JobRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    /**
     * Simplified search with only jobRole (title), location, and workMode.
     */
    public Page<JobDto> search(JobSearchRequest req, Pageable pageable) {
        Specification<Job> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("isActive")));

            if (req.getJobRole() != null && !req.getJobRole().isBlank()) {
                String like = "%" + req.getJobRole().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("title")), like));
            }

            if (req.getLocation() != null && !req.getLocation().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("location")),
                        "%" + req.getLocation().toLowerCase() + "%"));
            }

            if (req.getWorkMode() != null && !req.getWorkMode().isBlank()) {
                predicates.add(cb.equal(root.get("workMode"),
                        Job.WorkMode.valueOf(req.getWorkMode().toUpperCase())));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return jobRepository.findAll(spec, pageable).map(JobDto::fromEntity);
    }

    /** Get job role suggestions for autocomplete. */
    public List<String> getJobRoleSuggestions(String searchTerm) {
        return jobRepository.findDistinctTitles(searchTerm);
    }

    /** Get location suggestions for autocomplete. */
    public List<String> getLocationSuggestions(String searchTerm) {
        return jobRepository.findDistinctLocations(searchTerm);
    }

    public JobDto getById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + id));
        return JobDto.fromEntity(job);
    }

    public JobDto create(Job job) {
        return JobDto.fromEntity(jobRepository.save(job));
    }
}
