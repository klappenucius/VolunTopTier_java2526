package com.voluntoptier.project.service;

import com.voluntoptier.project.entities.Change;
import com.voluntoptier.project.entities.ChangeLog;
import com.voluntoptier.project.utils.FilesUtil;

import java.io.*;

public class ChangeLogService {

    private String changeLogFileName;
    private final File file;

    public ChangeLogService() throws IOException  {
        this.changeLogFileName = FilesUtil.getChangeLogFilePath();
        this.file = new File(this.changeLogFileName);
    }

    public void logChange (Change change) {
        ChangeLog loggedChangesList = deserializeAndloadChanges();
        loggedChangesList.add(change);
        serializeAndSaveChanges(loggedChangesList);
    }

    private ChangeLog deserializeAndloadChanges () {

        ChangeLog loggedChangesList;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject(); // readObject metoda deserijalizira sadržaj filea u objekt
            if (!(obj instanceof ChangeLog)) {
                throw new RuntimeException("Change log file contains unexpected data");
            }
             loggedChangesList = (ChangeLog) obj; // jer custom klasa ChangeLog (koja je samo wrapper za listu) implementira Serializable
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read change log file: " + changeLogFileName, e);
        }

        return loggedChangesList;
    }

    private void serializeAndSaveChanges(ChangeLog changeLog) {

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(changeLog); // writeObject metoda serijalizira objekt u sadržaj datoteke
        } catch (IOException e) {
            throw new RuntimeException("Failed to write into change log file: " + changeLogFileName, e);
        }
    }
}
