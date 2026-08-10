package com.vimalkumar.careerportal.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

/**
 * Generates a single-column, ATS-friendly PDF resume for a given tailored
 * version. Deliberately plain formatting (no tables/columns/graphics) because
 * that is exactly what makes a resume parse cleanly through ATS systems —
 * see the earlier resume review for why that matters.
 */
@Service
public class ResumeGeneratorService {

    public byte[] generate(String fullName, String targetRole, String summary,
                            List<String> matchedSkills, List<String> injectedKeywords)
            throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph(fullName).setBold().setFontSize(18));
            document.add(new Paragraph(targetRole).setFontSize(12).setItalic());
            document.add(new Paragraph(" "));

            document.add(new Paragraph("SUMMARY").setBold().setFontSize(13));
            document.add(new Paragraph(summary).setFontSize(10));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("SKILLS").setBold().setFontSize(13));
            document.add(new Paragraph(String.join(", ", matchedSkills)).setFontSize(10));

            if (injectedKeywords != null && !injectedKeywords.isEmpty()) {
                document.add(new Paragraph(" "));
                document.add(new Paragraph("TAILORED KEYWORDS").setBold().setFontSize(13));
                document.add(new Paragraph("This version includes the following target keywords:")
                        .setFontSize(9).setItalic().setTextAlignment(TextAlignment.LEFT));
                document.add(new Paragraph(String.join(", ", injectedKeywords)).setFontSize(10));
            }
        }

        return out.toByteArray();
    }
}
