package com.vimalkumar.careerportal.dto;

import com.vimalkumar.careerportal.entity.Job;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class JobDto {
    private Long id;
    private String title;
    private String companyName;
    private String description;
    private String techStack;
    private String location;
    private String workMode;
    private Double minExperience;
    private Double maxExperience;
    private Double salaryMin;
    private Double salaryMax;
    private String sourcePortal;
    private String sourceUrl;
    private LocalDate postedAt;

    public static JobDto fromEntity(Job job) {
        JobDto dto = new JobDto();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setCompanyName(job.getCompanyName());
        dto.setDescription(job.getDescription());
        dto.setTechStack(job.getTechStack());
        dto.setLocation(job.getLocation());
        dto.setWorkMode(job.getWorkMode() != null ? job.getWorkMode().name() : null);
        dto.setMinExperience(job.getMinExperience());
        dto.setMaxExperience(job.getMaxExperience());
        dto.setSalaryMin(job.getSalaryMin());
        dto.setSalaryMax(job.getSalaryMax());
        dto.setSourcePortal(job.getSourcePortal());
        dto.setSourceUrl(job.getSourceUrl());
        dto.setPostedAt(job.getPostedAt());
        return dto;
    }
}
