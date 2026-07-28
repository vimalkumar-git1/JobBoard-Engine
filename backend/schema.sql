-- ============================================================
-- Smart Job Aggregator, ATS Resume Matcher & Career Portal
-- MySQL 8.x Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS career_portal_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE career_portal_db;

-- ------------------------------------------------------------
-- USERS
-- ------------------------------------------------------------
CREATE TABLE users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(150)        NOT NULL,
    email           VARCHAR(150)        NOT NULL UNIQUE,
    password_hash   VARCHAR(255)        NOT NULL,
    role            ENUM('CANDIDATE', 'ADMIN') NOT NULL DEFAULT 'CANDIDATE',
    phone           VARCHAR(20),
    location         VARCHAR(150),
    total_experience_years DECIMAL(4,1) DEFAULT 0,
    created_at      DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP
                                         ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- JOBS  (aggregated listings)
-- ------------------------------------------------------------
CREATE TABLE jobs (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(200)        NOT NULL,
    company_name    VARCHAR(200)        NOT NULL,
    description     TEXT                NOT NULL,
    tech_stack      VARCHAR(500),        -- comma-separated tags, e.g. "Java,Spring Boot,MySQL"
    location        VARCHAR(150),
    work_mode       ENUM('REMOTE', 'HYBRID', 'ONSITE') DEFAULT 'ONSITE',
    min_experience  DECIMAL(4,1),
    max_experience  DECIMAL(4,1),
    salary_min      DECIMAL(12,2),
    salary_max      DECIMAL(12,2),
    source_portal   VARCHAR(100),        -- e.g. "Naukri", "Indeed", "Manual"
    source_url      VARCHAR(500),
    posted_at       DATE,
    is_active       BOOLEAN             NOT NULL DEFAULT TRUE,
    created_at      DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_jobs_location (location),
    INDEX idx_jobs_work_mode (work_mode),
    INDEX idx_jobs_experience (min_experience, max_experience),
    FULLTEXT INDEX ft_jobs_title_desc (title, description)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- RESUMES  (base uploaded resume per user)
-- ------------------------------------------------------------
CREATE TABLE resumes (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT              NOT NULL,
    original_filename VARCHAR(255)      NOT NULL,
    file_path       VARCHAR(500)        NOT NULL,
    parsed_text     LONGTEXT,
    parsed_skills   VARCHAR(1000),       -- comma-separated skills extracted by PDFBox parser
    uploaded_at     DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_resumes_user FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,
    INDEX idx_resumes_user (user_id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- RESUME_VERSIONS  (tailored/generated variants, version-controlled)
-- ------------------------------------------------------------
CREATE TABLE resume_versions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    resume_id       BIGINT              NOT NULL,
    user_id         BIGINT              NOT NULL,
    version_label   VARCHAR(100)        NOT NULL,  -- e.g. "v1_Java_Backend"
    version_number  INT                 NOT NULL,
    target_role     VARCHAR(150),
    matched_skills_snapshot VARCHAR(1000),
    generated_file_path VARCHAR(500),
    match_score     DECIMAL(5,2),        -- ATS match % at time of generation
    created_at      DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_resume_versions_resume FOREIGN KEY (resume_id) REFERENCES resumes(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_resume_versions_user FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,
    UNIQUE KEY uq_resume_version (resume_id, version_number),
    INDEX idx_resume_versions_user (user_id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- SAVED_SEARCHES  (for daily email alert matching)
-- ------------------------------------------------------------
CREATE TABLE saved_searches (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT              NOT NULL,
    search_name     VARCHAR(150)        NOT NULL,
    keywords        VARCHAR(300),
    location        VARCHAR(150),
    work_mode       ENUM('REMOTE', 'HYBRID', 'ONSITE', 'ANY') DEFAULT 'ANY',
    min_experience  DECIMAL(4,1),
    max_experience  DECIMAL(4,1),
    email_alerts_enabled BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at      DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_saved_searches_user FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,
    INDEX idx_saved_searches_user (user_id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- APPLICATIONS  (Kanban tracker, linked to resume version used)
-- ------------------------------------------------------------
CREATE TABLE applications (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT              NOT NULL,
    job_id          BIGINT              NOT NULL,
    resume_version_id BIGINT,            -- nullable: which tailored resume was used
    status          ENUM('SAVED', 'APPLIED', 'INTERVIEWING', 'OFFERED', 'REJECTED')
                                         NOT NULL DEFAULT 'SAVED',
    applied_at      DATETIME,
    cover_letter_text LONGTEXT,
    notes           TEXT,
    created_at      DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP
                                         ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_applications_user FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_applications_job FOREIGN KEY (job_id) REFERENCES jobs(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_applications_resume_version FOREIGN KEY (resume_version_id)
        REFERENCES resume_versions(id) ON DELETE SET NULL,
    UNIQUE KEY uq_user_job (user_id, job_id),
    INDEX idx_applications_status (status),
    INDEX idx_applications_user (user_id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- AUDIT_LOGS
-- ------------------------------------------------------------
CREATE TABLE audit_logs (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT,
    entity_name     VARCHAR(100)        NOT NULL,  -- e.g. "APPLICATION", "RESUME_VERSION"
    entity_id       BIGINT              NOT NULL,
    action          ENUM('CREATE', 'UPDATE', 'DELETE') NOT NULL,
    details         TEXT,
    performed_at    DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE SET NULL,
    INDEX idx_audit_logs_entity (entity_name, entity_id)
) ENGINE=InnoDB;
