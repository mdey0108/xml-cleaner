package com.example.xmlcleaner.service;

import com.example.xmlcleaner.config.AppProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class XmlProcessingService {
    private static final Logger logger = LogManager.getLogger(XmlProcessingService.class);

    private final AppProperties properties;

    // @Autowired
    public XmlProcessingService(AppProperties properties) {
        this.properties = properties;
    }

    public void processFiles() {
        if (properties.getFolders() == null || properties.getFolders().isEmpty()) {
            logger.warn("No folders configured for processing");
            return;
        }

        for (String folderPath : properties.getFolders()) {
            try {
                processFolder(Paths.get(folderPath));
            } catch (IOException e) {
                logger.error("Error processing folder: {}", folderPath, e);
            }
        }
    }

    private void processFolder(Path folder) throws IOException {
        if (!Files.exists(folder)) {
            logger.warn("Folder does not exist: {}", folder);
            return;
        }

        logger.info("Processing folder: {}", folder);

        try (Stream<Path> paths = Files.list(folder)) {
            List<Path> files = paths
                    .filter(Files::isRegularFile)
                    .filter(this::hasValidExtension)
                    .collect(Collectors.toList());

            for (Path file : files) {
                processFile(file);
            }
        }
    }

    private boolean hasValidExtension(Path file) {
        String fileName = file.getFileName().toString();
        return properties.getExtensions().stream()
                .anyMatch(ext -> fileName.toLowerCase().endsWith("." + ext.toLowerCase()));
    }

    // private void processFile(Path file) throws IOException {
    // logger.info("Processing file: {}", file);

    // // Backup original file if enabled
    // if (properties.isBackup()) {
    // backupFile(file);
    // }

    // // Read file content
    // String content = Files.readString(file);

    // // Remove invalid characters
    // String cleanedContent = cleanContent(content);

    // // Write cleaned content back to file
    // if (!content.equals(cleanedContent)) {
    // Files.writeString(file, cleanedContent);
    // logger.info("File cleaned successfully: {}", file);
    // } else {
    // logger.info("No invalid characters found in file: {}", file);
    // }
    // }

    private void processFile(Path file) throws IOException {
        logger.info("Processing file: {}", file);

        // Backup original file if enabled
        if (properties.isBackup()) {
            backupFile(file);
        }

        // Read file content (try UTF-8 first, then fallback to ANSI)
        String content;
        try {
            content = Files.readString(file, StandardCharsets.UTF_8);
        } catch (MalformedInputException e) {
            logger.debug("UTF-8 failed, trying ANSI (Windows-1252) for file: {}", file);
            content = Files.readString(file, Charset.forName("Windows-1252"));
        }

        // Clean the content
        String cleanedContent = cleanContent(content);

        // Always save as UTF-8
        if (!content.equals(cleanedContent)) {
            Files.writeString(file, cleanedContent, StandardCharsets.UTF_8);
            logger.info("File cleaned and converted to UTF-8: {}", file);
        } else {
            logger.info("No changes needed for file: {}", file);
        }
    }

    private void backupFile(Path file) throws IOException {
        Path backupDir = file.getParent().resolve(properties.getBackupDir());
        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir);
        }

        Path backupFile = backupDir.resolve(file.getFileName());
        Files.copy(file, backupFile, StandardCopyOption.REPLACE_EXISTING);
        logger.info("Backup created: {}", backupFile);
    }

    private String cleanContent(String content) {
        if (StringUtils.isBlank(properties.getInvalidCharsPattern())) {
            return content;
        }
        return content.replaceAll(properties.getInvalidCharsPattern(), " ");
    }
}