package com.vimalkumar.careerportal.service;

import com.vimalkumar.careerportal.dto.JobDto;
import com.vimalkumar.careerportal.dto.JobSearchRequest;
import com.vimalkumar.careerportal.entity.SavedSearch;
import com.vimalkumar.careerportal.repository.SavedSearchRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Daily email digest for saved searches.
 *
 * MVP note: a single @Scheduled cron job is enough here — there's no need
 * for Quartz/Spring Batch until you have thousands of saved searches and
 * need distributed job scheduling. Add that complexity only when you
 * actually hit a limitation this doesn't handle.
 */
@Service
public class EmailAlertService {

    private final SavedSearchRepository savedSearchRepository;
    private final JobService jobService;
    private final JavaMailSender mailSender;

    public EmailAlertService(SavedSearchRepository savedSearchRepository,
                              JobService jobService,
                              JavaMailSender mailSender) {
        this.savedSearchRepository = savedSearchRepository;
        this.jobService = jobService;
        this.mailSender = mailSender;
    }

    /** Runs once a day at 8 AM server time. */
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional(readOnly = true)
    public void sendDailyDigest() {
        List<SavedSearch> activeSearches = savedSearchRepository.findByEmailAlertsEnabledTrue();

        for (SavedSearch search : activeSearches) {
            sendDigestForSearch(search);
        }
    }

    private void sendDigestForSearch(SavedSearch search) {
        JobSearchRequest req = new JobSearchRequest();
        req.setJobRole(search.getKeywords());
        req.setLocation(search.getLocation());
        if (search.getWorkMode() != null && search.getWorkMode() != SavedSearch.WorkMode.ANY) {
            req.setWorkMode(search.getWorkMode().name());
        }

        Pageable topTen = PageRequest.of(0, 10);
        List<JobDto> matches = jobService.search(req, topTen).getContent();

        if (matches.isEmpty()) {
            return;
        }

        String body = "New jobs matching \"" + search.getSearchName() + "\":\n\n" +
                matches.stream()
                        .map(j -> "- " + j.getTitle() + " at " + j.getCompanyName() + " (" + j.getLocation() + ")")
                        .collect(Collectors.joining("\n"));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(search.getUser().getEmail());
        message.setSubject("Career Portal: " + matches.size() + " new jobs for \"" + search.getSearchName() + "\"");
        message.setText(body);

        mailSender.send(message);
    }
}
