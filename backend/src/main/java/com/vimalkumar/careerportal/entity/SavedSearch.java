package com.vimalkumar.careerportal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_searches")
@Getter
@Setter
public class SavedSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "search_name", nullable = false, length = 150)
    private String searchName;

    @Column(length = 300)
    private String keywords;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_mode")
    private WorkMode workMode = WorkMode.ANY;

    @Column(name = "min_experience")
    private Double minExperience;

    @Column(name = "max_experience")
    private Double maxExperience;

    @Column(name = "email_alerts_enabled", nullable = false)
    private Boolean emailAlertsEnabled = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum WorkMode {
        REMOTE, HYBRID, ONSITE, ANY
    }
}
