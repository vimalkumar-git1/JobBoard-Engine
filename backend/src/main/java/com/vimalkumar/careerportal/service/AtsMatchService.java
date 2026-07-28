package com.vimalkumar.careerportal.service;

import com.vimalkumar.careerportal.dto.AtsMatchResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The core "smart matcher" module: compares a candidate's parsed resume
 * skills against a job's required tech stack and produces a match score,
 * matched skills, and missing skills.
 *
 * This is the module worth spending the most care on — it's what makes
 * this project more than a CRUD app.
 */
@Service
public class AtsMatchService {

    public AtsMatchResponse computeMatch(List<String> resumeSkills, String jobTechStackCsv) {
        List<String> jobSkills = parseCsv(jobTechStackCsv);

        if (jobSkills.isEmpty()) {
            return new AtsMatchResponse(0.0, List.of(), List.of());
        }

        List<String> resumeSkillsLower = resumeSkills.stream()
                .map(s -> s.toLowerCase(Locale.ROOT).trim())
                .toList();

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String jobSkill : jobSkills) {
            boolean isMatched = resumeSkillsLower.contains(jobSkill.toLowerCase(Locale.ROOT).trim());
            if (isMatched) {
                matched.add(jobSkill);
            } else {
                missing.add(jobSkill);
            }
        }

        double score = (matched.size() * 100.0) / jobSkills.size();
        double rounded = Math.round(score * 100.0) / 100.0;

        return new AtsMatchResponse(rounded, matched, missing);
    }

    private List<String> parseCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
