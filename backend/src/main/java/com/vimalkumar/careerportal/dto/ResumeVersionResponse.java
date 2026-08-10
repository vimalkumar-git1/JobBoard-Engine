package com.vimalkumar.careerportal.dto;

import com.vimalkumar.careerportal.entity.ResumeVersion;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumeVersionResponse {
    private Long id;
    private String versionLabel;
    private Integer versionNumber;
    private String targetRole;
    private Double matchScore;
    private String matchedSkillsSnapshot;
    private String downloadUrl;

    public static ResumeVersionResponse fromEntity(ResumeVersion v) {
        ResumeVersionResponse r = new ResumeVersionResponse();
        r.setId(v.getId());
        r.setVersionLabel(v.getVersionLabel());
        r.setVersionNumber(v.getVersionNumber());
        r.setTargetRole(v.getTargetRole());
        r.setMatchScore(v.getMatchScore());
        r.setMatchedSkillsSnapshot(v.getMatchedSkillsSnapshot());
        r.setDownloadUrl("/resumes/versions/" + v.getId() + "/download");
        return r;
    }
}
