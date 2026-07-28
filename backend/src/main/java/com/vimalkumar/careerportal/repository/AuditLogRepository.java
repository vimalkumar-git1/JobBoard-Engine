package com.vimalkumar.careerportal.repository;

import com.vimalkumar.careerportal.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
