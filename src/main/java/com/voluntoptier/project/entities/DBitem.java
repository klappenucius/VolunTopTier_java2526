package com.voluntoptier.project.entities;

public abstract class DBitem {
    private int id;

    protected abstract void print();

    @Override
    public abstract String toString();

    public DBitem(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
