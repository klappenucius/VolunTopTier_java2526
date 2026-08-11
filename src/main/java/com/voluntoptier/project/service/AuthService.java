package com.voluntoptier.project.service;

import com.voluntoptier.project.utils.FilesUtil;
import com.voluntoptier.project.utils.HashUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthService {

    private String credentialsFileName;

    private final Map<String, String> usersCreds;

    public AuthService(String credentialsFileName) throws IOException {
        this.credentialsFileName = FilesUtil.getCredentialsFilePath();
        this.usersCreds = loadCreds();
    }

    private Map<String, String> loadCreds() {
        InputStream in = getClass().getClassLoader().getResourceAsStream(credentialsFileName);
        Map<String, String> loadedCreds;

        if (in == null) {
            throw new RuntimeException("Credentials file not found on classpath: " + credentialsFileName);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

            loadedCreds = reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(line -> line.split(",", 2))
                    .filter(parts -> parts.length == 2)
                    .collect(Collectors.toMap(
                            parts -> parts[0].trim(),
                            parts -> parts[1].trim()
                    ));

            return loadedCreds;

        } catch (IOException e) {
            throw new RuntimeException("Failed to read credentials file: " + credentialsFileName, e);
        }
    }

    public boolean validateCreds(String username, String password) {

        String hashedPwd = HashUtil.hash(password);
            String storedPwdHash = usersCreds.get(username);
            return hashedPwd.equals(storedPwdHash);
    }
}
