package com.vimalkumar.careerportal.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResumeParserService {

    /**
     * Extracts raw text from an uploaded PDF resume using Apache PDFBox.
     */
    public String extractText(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * Naive but effective skill extraction: check which known skills appear
     * as substrings in the parsed resume text (case-insensitive).
     * Good enough for an MVP; a real NLP-based extractor is a fair Phase 2 upgrade.
     */
    public List<String> extractSkills(String resumeText) {
        if (resumeText == null || resumeText.isBlank()) {
            return List.of();
        }
        String lower = resumeText.toLowerCase();
        List<String> found = new ArrayList<>();

        for (String skill : SkillDictionary.KNOWN_SKILLS) {
            if (lower.contains(skill.toLowerCase())) {
                found.add(skill);
            }
        }
        return found;
    }
}
