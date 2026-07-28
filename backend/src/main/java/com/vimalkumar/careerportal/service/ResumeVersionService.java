package com.vimalkumar.careerportal.service;

import com.vimalkumar.careerportal.entity.Resume;
import com.vimalkumar.careerportal.entity.ResumeVersion;
import com.vimalkumar.careerportal.entity.User;
import com.vimalkumar.careerportal.repository.ResumeVersionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeVersionService {

    private final ResumeVersionRepository resumeVersionRepository;

    public ResumeVersionService(ResumeVersionRepository resumeVersionRepository) {
        this.resumeVersionRepository = resumeVersionRepository;
    }

    /** Computes the next version number/label without persisting anything yet,
     *  so the caller can name the generated PDF file to match before saving the record. */
    public String peekNextVersionLabel(Long resumeId, String targetRole) {
        long existingCount = resumeVersionRepository.countByResumeId(resumeId);
        int nextVersionNumber = (int) existingCount + 1;
        return "v" + nextVersionNumber + "_" + targetRole.replaceAll("\\s+", "_");
    }

    public ResumeVersion createVersion(Resume resume, User user, String targetRole,
                                        List<String> matchedSkills, Double matchScore,
                                        String generatedFilePath) {

        long existingCount = resumeVersionRepository.countByResumeId(resume.getId());
        int nextVersionNumber = (int) existingCount + 1;

        String label = "v" + nextVersionNumber + "_" + targetRole.replaceAll("\\s+", "_");

        ResumeVersion version = new ResumeVersion();
        version.setResume(resume);
        version.setUser(user);
        version.setVersionNumber(nextVersionNumber);
        version.setVersionLabel(label);
        version.setTargetRole(targetRole);
        version.setMatchedSkillsSnapshot(String.join(",", matchedSkills));
        version.setMatchScore(matchScore);
        version.setGeneratedFilePath(generatedFilePath);

        return resumeVersionRepository.save(version);
    }

    public List<ResumeVersion> getVersionsForUser(Long userId) {
        return resumeVersionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
