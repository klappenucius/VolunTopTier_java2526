package com.voluntoptier.project.service;

import com.voluntoptier.project.entities.Change;
import com.voluntoptier.project.entities.ChangeLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Objects;

/**
 * Keeps the history of all data changes in a binary file, using serialization.
 */
@Service
public class ChangeLogService {

    private static final Logger logger = LoggerFactory.getLogger(ChangeLogService.class);

    private final File file;

    /**
     * @param changeLogFileName path of the change log file, from {@code voluntoptier.files.change-log}
     */
    public ChangeLogService(@Value("${voluntoptier.files.change-log}") String changeLogFileName) {
        this.file = new File(changeLogFileName);
    }

    /**
     * Appends one change to the change log file. {@code synchronized} because several requests can
     * log at the same time, and two read-modify-write cycles at once would lose an entry.
     *
     * @param change the change to record
     */
    public synchronized void logChange(Change change) {
        ChangeLog loggedChangesList = deserializeAndLoadChanges();
        loggedChangesList.add(change);
        serializeAndSaveChanges(loggedChangesList);
    }

    /**
     * Like {@link #logChange(Change)}, but a failure is only logged: a broken change log must never
     * stop the actual data change.
     *
     * @param change the change to record
     */
    public void logChangeSafely(Change change) {
        try {
            logChange(change);
        } catch (RuntimeException e) {
            logger.error("Failed to write change log entry for {} id={}",
                    change.getEntityType(), change.getEntityId(), e);
        }
    }

    /**
     * Adds an UPDATE change to {@code changes} if the old and new value differ (null-safe).
     *
     * @param changes    list the change is added to
     * @param entityType entity name, e.g. "Project"
     * @param entityId   id of the changed entity
     * @param field      name of the changed field
     * @param oldValue   value before the change
     * @param newValue   value after the change
     * @param changedBy  username of the person making the change
     */
    public static void addIfChanged(List<Change> changes, String entityType, int entityId, String field,
                                    Object oldValue, Object newValue, String changedBy) {
        if (!Objects.equals(oldValue, newValue)) {
            changes.add(new Change(entityType, entityId, "UPDATE", field,
                    oldValue == null ? null : oldValue.toString(),
                    newValue == null ? null : newValue.toString(), changedBy));
        }
    }

    private ChangeLog deserializeAndLoadChanges() {
        if (!file.exists() || file.length() == 0) {
            return new ChangeLog();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject(); // readObject metoda deserijalizira sadržaj filea u objekt
            if (!(obj instanceof ChangeLog changeLog)) {
                throw new IllegalStateException("Change log file contains unexpected data: " + file);
            }
            return changeLog;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Failed to read change log file: " + file, e);
        }
    }

    private void serializeAndSaveChanges(ChangeLog changeLog) {
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Could not create folder for change log: " + parent);
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(changeLog); // writeObject metoda serijalizira objekt u sadržaj datoteke
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write into change log file: " + file, e);
        }
    }
}
