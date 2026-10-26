package com.voluntoptier.project.service;

import com.voluntoptier.project.utils.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Checks login credentials against a text file of {@code username,sha256hash} lines.
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final Path credentialsFile;
    private final Map<String, String> usersCreds;

    /**
     * Loads all credentials once, when the application starts.
     *
     * @param credentialsFileName path of the credentials file, from {@code voluntoptier.files.credentials}
     */
    public AuthService(@Value("${voluntoptier.files.credentials}") String credentialsFileName) {
        this.credentialsFile = Path.of(credentialsFileName);
        this.usersCreds = loadCreds();
    }

    private Map<String, String> loadCreds() {
        if (!Files.exists(credentialsFile)) {
            throw new IllegalStateException("Credentials file not found: " + credentialsFile.toAbsolutePath());
        }

        try (Stream<String> lines = Files.lines(credentialsFile, StandardCharsets.UTF_8)) {
            Map<String, String> loadedCreds = lines
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(line -> line.split(",", 2))
                    .filter(parts -> parts.length == 2)
                    .collect(Collectors.toMap(
                            parts -> parts[0].trim(),
                            parts -> parts[1].trim()
                    ));

            logger.info("Loaded {} credentials from {}", loadedCreds.size(), credentialsFile);
            return Map.copyOf(loadedCreds);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read credentials file: " + credentialsFile, e);
        }
    }

    /**
     * @param username the entered username
     * @param password the entered password, in plain text
     * @return {@code true} if the username exists and the password's hash matches the stored one
     */
    public boolean validateCreds(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        String storedPwdHash = usersCreds.get(username);
        return storedPwdHash != null && storedPwdHash.equals(HashUtil.hash(password));
    }
}
