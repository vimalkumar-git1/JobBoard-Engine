package com.vimalkumar.careerportal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "resume_versions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"resume_id", "version_number"}))
@Getter
@Setter
public class ResumeVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "version_label", nullable = false, length = 100)
    private String versionLabel;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "target_role", length = 150)
    private String targetRole;

    @Column(name = "matched_skills_snapshot", length = 1000)
    private String matchedSkillsSnapshot;

    @Column(name = "generated_file_path", length = 500)
    private String generatedFilePath;

    @Column(name = "match_score")
    private Double matchScore;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
