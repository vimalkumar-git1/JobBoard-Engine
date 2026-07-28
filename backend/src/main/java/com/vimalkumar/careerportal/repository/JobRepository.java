package com.vimalkumar.careerportal.repository;

import com.vimalkumar.careerportal.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    /** Used by the aggregator to skip re-inserting a job it has already pulled. */
    boolean existsBySourceUrl(String sourceUrl);
}
