package com.vimalkumar.careerportal.dto;

import com.vimalkumar.careerportal.entity.Application;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationDto {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private Long resumeVersionId;
    private String resumeVersionLabel;
    private String status;
    private LocalDateTime appliedAt;
    private String notes;

    public static ApplicationDto fromEntity(Application app) {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(app.getId());
        dto.setJobId(app.getJob().getId());
        dto.setJobTitle(app.getJob().getTitle());
        dto.setCompanyName(app.getJob().getCompanyName());
        if (app.getResumeVersion() != null) {
            dto.setResumeVersionId(app.getResumeVersion().getId());
            dto.setResumeVersionLabel(app.getResumeVersion().getVersionLabel());
        }
        dto.setStatus(app.getStatus().name());
        dto.setAppliedAt(app.getAppliedAt());
        dto.setNotes(app.getNotes());
        return dto;
    }
}
