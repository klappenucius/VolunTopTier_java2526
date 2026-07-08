package com.voluntoptier.project.entities;

import java.time.LocalDate;

public class Project extends DBitem {

    protected String name;

    protected Address address;

    protected Integer totalHours;

    protected Integer hoursWorked;

    protected LocalDate startDate;

    protected LocalDate endDate;

    protected int volunteersNeeded;

    public Project(String id, String name, Integer totalHours, LocalDate startDate, LocalDate endDate, int volunteersNeeded) {
        super(id);
        this.name = name;
        this.totalHours = totalHours;
        this.hoursWorked = 0;
        this.startDate = startDate;
        this.endDate = endDate;
        this.volunteersNeeded = volunteersNeeded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Integer getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Integer totalHours) {
        this.totalHours = totalHours;
    }

    public Integer getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(Integer hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getVolunteersNeeded() {
        return volunteersNeeded;
    }

    public void setVolunteersNeeded(int volunteersNeeded) {
        this.volunteersNeeded = volunteersNeeded;
    }

    public String toString () {
        String output = name +
                ", total duration: " + totalHours +
                ", hours worked: " + hoursWorked +
                ", start date: " + startDate.toString() +
                ", end date: " + endDate.toString() +
                ", number of volunteers: " + volunteersNeeded;
        return output;
    }

    public void print() {
        System.out.println(this.toString());
    }
}
