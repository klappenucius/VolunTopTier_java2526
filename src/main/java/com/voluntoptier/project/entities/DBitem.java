package com.voluntoptier.project.entities;

public abstract class DBitem {
    private String id;

    protected abstract void print();

    @Override
    public abstract String toString();

    public DBitem(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
