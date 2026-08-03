package com.voluntoptier.project.utils;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {

    private static final String DATABASE_FILE = "database.properties";

    private static Connection connectToDatabase() throws SQLException, IOException {
        try (var reader = new FileReader(DATABASE_FILE)) {
            var properties = new Properties();
            properties.load(reader);

            var url = properties.getProperty("databaseUrl");
            var user = properties.getProperty("username");
            var password = properties.getProperty("password");

            return DriverManager.getConnection(url, user, password);
        }
    }
}
