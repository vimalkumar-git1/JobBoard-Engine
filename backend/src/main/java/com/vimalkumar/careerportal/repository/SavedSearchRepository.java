package com.vimalkumar.careerportal.repository;

import com.vimalkumar.careerportal.entity.SavedSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedSearchRepository extends JpaRepository<SavedSearch, Long> {
    List<SavedSearch> findByEmailAlertsEnabledTrue();
    List<SavedSearch> findByUserId(Long userId);
}
