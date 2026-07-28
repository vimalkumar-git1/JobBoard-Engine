package com.vimalkumar.careerportal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Getter
@Setter
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column(name = "tech_stack", length = 500)
    private String techStack;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_mode")
    private WorkMode workMode = WorkMode.ONSITE;

    @Column(name = "min_experience")
    private Double minExperience;

    @Column(name = "max_experience")
    private Double maxExperience;

    @Column(name = "salary_min")
    private Double salaryMin;

    @Column(name = "salary_max")
    private Double salaryMax;

    @Column(name = "source_portal", length = 100)
    private String sourcePortal;

    @Column(name = "source_url", length = 500)
    private String sourceUrl;

    @Column(name = "posted_at")
    private LocalDate postedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum WorkMode {
        REMOTE, HYBRID, ONSITE
    }
}
