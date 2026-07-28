package com.vimalkumar.careerportal.repository;

import com.vimalkumar.careerportal.entity.ResumeVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeVersionRepository extends JpaRepository<ResumeVersion, Long> {
    List<ResumeVersion> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<ResumeVersion> findByResumeIdOrderByVersionNumberDesc(Long resumeId);
    long countByResumeId(Long resumeId);
}
