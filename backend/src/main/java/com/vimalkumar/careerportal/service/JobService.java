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
     * Multi-criteria dynamic search using JPA Specifications.
     * Each filter is only applied if the caller actually provided it —
     * this is the pattern that scales to "add one more filter" later
     * without rewriting the query.
     */
    public Page<JobDto> search(JobSearchRequest req, Pageable pageable) {
        Specification<Job> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("isActive")));

            if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
                String like = "%" + req.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("description")), like)
                ));
            }

            if (req.getTechStack() != null && !req.getTechStack().isBlank()) {
                for (String tech : req.getTechStack().split(",")) {
                    predicates.add(cb.like(cb.lower(root.get("techStack")),
                            "%" + tech.trim().toLowerCase() + "%"));
                }
            }

            if (req.getLocation() != null && !req.getLocation().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("location")),
                        "%" + req.getLocation().toLowerCase() + "%"));
            }

            if (req.getWorkMode() != null && !req.getWorkMode().isBlank()) {
                predicates.add(cb.equal(root.get("workMode"),
                        Job.WorkMode.valueOf(req.getWorkMode().toUpperCase())));
            }

            if (req.getMinExperience() != null) {
                predicates.add(cb.or(
                        cb.isNull(root.get("maxExperience")),
                        cb.greaterThanOrEqualTo(root.get("maxExperience"), req.getMinExperience())
                ));
            }

            if (req.getMaxExperience() != null) {
                predicates.add(cb.or(
                        cb.isNull(root.get("minExperience")),
                        cb.lessThanOrEqualTo(root.get("minExperience"), req.getMaxExperience())
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return jobRepository.findAll(spec, pageable).map(JobDto::fromEntity);
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
