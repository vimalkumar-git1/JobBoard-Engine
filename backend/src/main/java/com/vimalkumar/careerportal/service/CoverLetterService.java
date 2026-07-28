package com.vimalkumar.careerportal.service;

import org.springframework.stereotype.Service;

/**
 * MVP cover letter generator: a structured template filled in from the
 * candidate's profile and the job. This is intentionally template-based
 * rather than calling an external LLM API, so the module has zero external
 * dependencies and no extra API key/cost to manage while you're learning
 * the rest of the stack. Swapping the body of generate() for a call to an
 * LLM API later is a clean, isolated upgrade — the rest of the app doesn't change.
 */
@Service
public class CoverLetterService {

    public String generate(String candidateName, String jobTitle, String companyName,
                            String matchedSkillsCsv) {

        return """
                Dear Hiring Manager,

                I am writing to express my interest in the %s position at %s. With hands-on
                experience in %s, I believe I can contribute effectively to your team from day one.

                I would welcome the opportunity to discuss how my background aligns with your
                team's needs.

                Regards,
                %s
                """.formatted(jobTitle, companyName, matchedSkillsCsv, candidateName);
    }
}
