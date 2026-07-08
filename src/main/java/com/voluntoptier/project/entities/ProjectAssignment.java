package com.voluntoptier.project.entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class ProjectAssignment extends DBitem{
    protected LocalDate date;

    protected LocalTime time;

    protected User assignedby;

    protected Project project;

    protected User user;

    protected int expectedHours;

    protected HoursWorked hoursWorked;

    public ProjectAssignment(String id, Project project, User user, int expectedHours, HoursWorked hoursWorked) {
        super(id);
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        //this.assignedby = assignedby;
        this.project = project;
        this.user = user;
        this.expectedHours = expectedHours;
        this.hoursWorked = hoursWorked;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public User getAssignedby() {
        return assignedby;
    }

    public void setAssignedby(User assignedby) {
        this.assignedby = assignedby;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public int getExpectedHours() {
        return expectedHours;
    }

    public void setExpectedHours(int expectedHours) {
        this.expectedHours = expectedHours;
    }

    public HoursWorked getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(HoursWorked hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public String toString() {
        String output = "User " + user.toString() +
                " has been assigned to " + project.toString() +
                " on " + date.toString() +
                " at " + time.toString() +
                " by " + assignedby.toString() +
                ", expected hours: " + expectedHours;
        return output;
    }

    public void print() {
        System.out.println(this.toString());
    }
}
