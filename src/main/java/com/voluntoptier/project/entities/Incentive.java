package com.voluntoptier.project.entities;

public class Incentive extends DBitem {
    protected String name;
    protected Provider provider;
    protected String description;

    public Incentive(String id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        String output = name +
                ": " + description;
        return output;
    }

    public void print(){
        System.out.println(this.toString());
    }
}
