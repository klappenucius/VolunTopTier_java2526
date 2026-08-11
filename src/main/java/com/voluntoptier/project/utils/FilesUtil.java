package com.voluntoptier.project.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class FilesUtil {
    private static final String PROPERTIES_FILE = "files.properties";

    public static String getCredentialsFilePath() throws IOException {
        try (var reader = new FileReader(PROPERTIES_FILE)) {
            var properties = new Properties();
            properties.load(reader);

            var credentialsFilePath = properties.getProperty("credentials");
           return credentialsFilePath.toString();
        }
    }

    public static String getChangeLogFilePath() throws IOException {
        try (var reader = new FileReader(PROPERTIES_FILE)) {
            var properties = new Properties();
            properties.load(reader);

            var credentialsFilePath = properties.getProperty("changeLog");
            return credentialsFilePath.toString();
        }
    }
}
