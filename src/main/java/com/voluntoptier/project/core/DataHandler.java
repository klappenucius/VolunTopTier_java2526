package com.voluntoptier.project.core;

import java.nio.file.Path;

public interface DataHandler {
    void write(Path destPath, Object object) throws Exception;
    <T> T read(Path srcPath, Class<T> type) throws Exception;
}
