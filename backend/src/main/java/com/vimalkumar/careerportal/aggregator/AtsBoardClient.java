package com.vimalkumar.careerportal.aggregator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Calls the PUBLIC, no-key-required job-board APIs that Greenhouse and Lever
 * expose for companies that host their careers page on those platforms.
 * These are official, documented endpoints — not scraping.
 *
 * Greenhouse docs: https://developers.greenhouse.io/job-board.html
 * Lever docs:       https://github.com/lever/postings-api
 */
@Slf4j
@Component
public class AtsBoardClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    /** Normalized job pulled from either ATS, ready to map into our Job entity. */
    public record NormalizedJob(String title, String company, String description,
                                 String location, String sourceUrl, LocalDate postedAt) {
    }

    public List<NormalizedJob> fetch(CompanyAtsSource source) {
        try {
            return switch (source.atsType().toLowerCase()) {
                case "greenhouse" -> fetchGreenhouse(source, "boards-api.greenhouse.io");
                case "greenhouse-eu" -> fetchGreenhouse(source, "boards-api.eu.greenhouse.io");
                case "lever" -> fetchLever(source);
                default -> {
                    log.warn("Unknown atsType '{}' for company '{}'", source.atsType(), source.companyDisplayName());
                    yield List.of();
                }
            };
        } catch (Exception ex) {
            log.error("ATS fetch failed for {} ({}): {}", source.companyDisplayName(), source.atsType(), ex.getMessage());
            return List.of();
        }
    }

    private List<NormalizedJob> fetchGreenhouse(CompanyAtsSource source, String host) {
        String url = "https://" + host + "/v1/boards/" + source.boardSlug() + "/jobs?content=true";
        String body = restTemplate.getForObject(url, String.class);
        List<NormalizedJob> jobs = new ArrayList<>();
        if (body == null) return jobs;

        try {
            JsonNode root = mapper.readTree(body);
            JsonNode jobsNode = root.path("jobs");
            for (JsonNode j : jobsNode) {
                String title = j.path("title").asText("Untitled role");
                String location = j.path("location").path("name").asText(null);
                String absoluteUrl = j.path("absolute_url").asText(null);
                String content = j.path("content").asText(""); // HTML description
                LocalDate posted = parseIsoDate(j.path("updated_at").asText(null));
                jobs.add(new NormalizedJob(title, source.companyDisplayName(), content, location, absoluteUrl, posted));
            }
        } catch (Exception e) {
            log.error("Failed parsing Greenhouse response for {}: {}", source.companyDisplayName(), e.getMessage());
        }
        return jobs;
    }

    private List<NormalizedJob> fetchLever(CompanyAtsSource source) {
        String url = "https://api.lever.co/v0/postings/" + source.boardSlug() + "?mode=json";
        String body = restTemplate.getForObject(url, String.class);
        List<NormalizedJob> jobs = new ArrayList<>();
        if (body == null) return jobs;

        try {
            JsonNode root = mapper.readTree(body);
            for (JsonNode j : root) {
                String title = j.path("text").asText("Untitled role");
                String location = j.path("categories").path("location").asText(null);
                String hostedUrl = j.path("hostedUrl").asText(null);
                String description = j.path("descriptionPlain").asText(j.path("description").asText(""));
                long createdMillis = j.path("createdAt").asLong(0);
                LocalDate posted = createdMillis > 0
                        ? Instant.ofEpochMilli(createdMillis).atZone(ZoneId.systemDefault()).toLocalDate()
                        : LocalDate.now();
                jobs.add(new NormalizedJob(title, source.companyDisplayName(), description, location, hostedUrl, posted));
            }
        } catch (Exception e) {
            log.error("Failed parsing Lever response for {}: {}", source.companyDisplayName(), e.getMessage());
        }
        return jobs;
    }

    private LocalDate parseIsoDate(String iso) {
        if (iso == null || iso.isBlank()) return LocalDate.now();
        try {
            return Instant.parse(iso).atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
}
