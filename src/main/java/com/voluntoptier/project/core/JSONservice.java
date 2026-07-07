package com.voluntoptier.project.core;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Utility class for JSON serialization and deserialization using Jakarta JSON-B.
 */
public class JSONservice implements DataHandler {

    private static final Jsonb JSONB = JsonbBuilder.create(
            new JsonbConfig()
                    .withFormatting(true)
    );

    /**
     * Writes an object to a JSON file.
     *
     * @param destPath path to write the JSON file to
     * @param object   object to serialize
     */
    //public static void serializeToFile(Path destPath, Object object) throws IOException {
        //Objects.requireNonNull(destPath, "destPath");
        //Objects.requireNonNull(object, "object");

        // Ensure parent folder exists
        //if (destPath.getParent() != null) {
          //  Files.createDirectories(destPath.getParent());
        //}

        //try (Writer writer = Files.newBufferedWriter(destPath)) {
          //  JSONB.toJson(object, writer);
        //}
    public void write(Path destPath, Object object) throws IOException {
        if (destPath.getParent() != null) {
            Files.createDirectories(destPath.getParent());
        }
        try (Writer writer = Files.newBufferedWriter(destPath)) {
            JSONB.toJson(object, writer);
        }
    }

    public <T> T read(Path srcPath, Class<T> type) throws IOException {
        try (Reader reader = Files.newBufferedReader(srcPath)) {
            return JSONB.fromJson(reader, type);
        }
    }
}



    /**
     * Reads an object of type T from a JSON file.
     *
     * @param srcPath the path to read from
     * @param type    the target class type
     * @param <T>     generic type parameter
     * @return deserialized object of type T
     */
    //public static <T> T deserializeFromFile(Path srcPath, Class<T> type) throws IOException {
      //  Objects.requireNonNull(srcPath, "srcPath");
        //Objects.requireNonNull(type, "type");

        //try (Reader reader = Files.newBufferedReader(srcPath)) {
          //  return JSONB.fromJson(reader, type);
        //}
    //}


