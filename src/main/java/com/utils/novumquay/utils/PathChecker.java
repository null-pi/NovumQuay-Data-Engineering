package com.utils.novumquay.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PathChecker {
    
    private String path;

    private String pathType;

    public PathChecker(String path, String pathType) {
        this.path = path;
        this.pathType = pathType;
    }

    public void ensurePathExists() {
        try {
            Path filePath = Paths.get(this.path);

            if (Files.exists(filePath)) {
                if (this.pathType == "dir" && !Files.isDirectory(filePath)) {
                    log.error("Path {} of type {} already exists but is not a directory: {}", pathType, path, filePath);
                    throw new RuntimeException("Path exists but is not a directory: " + filePath);
                } else if (this.pathType == "file" && !Files.isRegularFile(filePath)) {
                    log.error("Path {} of type {} already exists but is not a file: {}", pathType, path, filePath);
                    throw new RuntimeException("Path exists but is not a file: " + filePath);
                } else {
                    log.info("Path {} of type {} already exists: {}", pathType, path, filePath);
                }
            } else {
                log.info("Creating path {} of type {}: {}", pathType, path, filePath);
                if (this.pathType.equals("dir")) {
                    Files.createDirectories(filePath);
                } else if (this.pathType.equals("file")) {
                    Files.createFile(filePath);
                } else {
                    log.error("Invalid path type specified: {}", this.pathType);
                    throw new IllegalArgumentException("Invalid path type: " + this.pathType);
                }
                log.info("Path created successfully: {}", filePath);
            }
        } catch (Exception e) {
            log.error("Error checking path: {} of type: {}", path, pathType, e);
            throw new RuntimeException("Path check failed for " + pathType + ": " + path, e);
        } finally {
            log.info("Path check completed for {}: {}", pathType, path);
        }
    }

}
