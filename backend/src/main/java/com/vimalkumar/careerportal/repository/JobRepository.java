package com.vimalkumar.careerportal.repository;

import com.vimalkumar.careerportal.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    /** Used by the aggregator to skip re-inserting a job it has already pulled. */
    boolean existsBySourceUrl(String sourceUrl);

    /** Get distinct job titles matching the search prefix for autocomplete. */
    @Query("SELECT DISTINCT LOWER(j.title) FROM Job j WHERE j.isActive = true AND LOWER(j.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY LOWER(j.title)")
    List<String> findDistinctTitles(@Param("searchTerm") String searchTerm);

    /** Get distinct locations matching the search prefix for autocomplete. */
    @Query("SELECT DISTINCT LOWER(j.location) FROM Job j WHERE j.isActive = true AND LOWER(j.location) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY LOWER(j.location)")
    List<String> findDistinctLocations(@Param("searchTerm") String searchTerm);
}
