package com.example.xmlcleaner;

import com.example.xmlcleaner.service.XmlProcessingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class XmlCleanerApplication implements CommandLineRunner {
    private static final Logger logger = LogManager.getLogger(XmlCleanerApplication.class);

    private final XmlProcessingService xmlProcessingService;

    public XmlCleanerApplication(XmlProcessingService xmlProcessingService) {
        this.xmlProcessingService = xmlProcessingService;
    }

    public static void main(String[] args) {
        SpringApplication.run(XmlCleanerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        logger.info("Starting XML cleaner application");
        try {
            xmlProcessingService.processFiles();
            logger.info("XML processing completed successfully");
        } catch (Exception e) {
            logger.error("Error during XML processing", e);
        }
    }
}