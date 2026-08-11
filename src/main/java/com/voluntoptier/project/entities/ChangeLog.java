package com.voluntoptier.project.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChangeLog implements Serializable {
    private List<Change> changes = new ArrayList<>();

    public void add(Change change) {
        changes.add(change);
    }

    public List<Change> getChanges() {
        return changes;
    }
}
