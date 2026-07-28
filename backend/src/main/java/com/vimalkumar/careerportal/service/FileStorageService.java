package com.vimalkumar.careerportal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Local-disk file storage for uploaded/generated resume files.
 * Swap this for an S3-backed implementation later without touching
 * any calling code — that's the point of keeping it behind one interface-like service.
 */
@Service
public class FileStorageService {

    @Value("${app.storage.root}")
    private String storageRoot;

    public String saveUploadedResume(Long userId, String originalFilename, byte[] content) throws IOException {
        Path dir = Paths.get(storageRoot, "resumes", String.valueOf(userId));
        Files.createDirectories(dir);
        String safeName = System.currentTimeMillis() + "_" + originalFilename.replaceAll("\\s+", "_");
        Path target = dir.resolve(safeName);
        Files.write(target, content);
        return target.toString();
    }

    public String saveGeneratedResume(Long userId, String versionLabel, byte[] content) throws IOException {
        Path dir = Paths.get(storageRoot, "generated", String.valueOf(userId));
        Files.createDirectories(dir);
        Path target = dir.resolve(versionLabel + ".pdf");
        Files.write(target, content);
        return target.toString();
    }

    public byte[] read(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }
}
