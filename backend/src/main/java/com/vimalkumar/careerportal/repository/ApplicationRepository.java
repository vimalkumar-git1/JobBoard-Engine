package com.vimalkumar.careerportal.repository;

import com.vimalkumar.careerportal.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUserIdOrderByUpdatedAtDesc(Long userId);
    Optional<Application> findByUserIdAndJobId(Long userId, Long jobId);
}
