package com.example.xmlcleaner.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "xml.cleaner")
public class AppProperties {
    private List<String> folders;
    private List<String> extensions;
    private boolean backup;
    private String backupDir;
    private String invalidCharsPattern;

    // Getters and Setters
    public List<String> getFolders() {
        return folders;
    }

    public void setFolders(List<String> folders) {
        this.folders = folders;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<String> extensions) {
        this.extensions = extensions;
    }

    public boolean isBackup() {
        return backup;
    }

    public void setBackup(boolean backup) {
        this.backup = backup;
    }

    public String getBackupDir() {
        return backupDir;
    }

    public void setBackupDir(String backupDir) {
        this.backupDir = backupDir;
    }

    public String getInvalidCharsPattern() {
        return invalidCharsPattern;
    }

    public void setInvalidCharsPattern(String invalidCharsPattern) {
        this.invalidCharsPattern = invalidCharsPattern;
    }
}