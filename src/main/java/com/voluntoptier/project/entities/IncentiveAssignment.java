package com.voluntoptier.project.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class IncentiveAssignment extends DBitem{
    protected Incentive incentive;
    protected Project project;
    protected LocalDate assignmentDate;
    protected LocalTime assignmentTime;
    protected User assignedBy;
    protected int hoursNeeded;

    public IncentiveAssignment(int id, LocalDate assignmentDate, LocalTime assignmentTime, Incentive incentive, Project project, User assignedBy, int hoursNeeded) {
        super(id);
        this.incentive = incentive;
        this.project = project;
        this.assignmentDate = assignmentDate;
        this.assignmentTime = assignmentTime;
        this.assignedBy = assignedBy;
        this.hoursNeeded = hoursNeeded;
    }

    public Incentive getIncentive() {
        return incentive;
    }

    public void setIncentive(Incentive incentive) {
        this.incentive = incentive;
    }

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public LocalTime getAssignmentTime() {
        return assignmentTime;
    }

    public void setAssignmentTime(LocalTime assignmentTime) {
        this.assignmentTime = assignmentTime;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public int getHoursNeeded() {
        return hoursNeeded;
    }

    public void setHoursNeeded(int hoursNeeded) {
        this.hoursNeeded = hoursNeeded;
    }

    public User getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(User assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String toString() {
        String output = "Incentive " + incentive.toString() +
                " has been assigned to project " + project.toString() +
                ", hours threshold to obtain incentive: " + hoursNeeded;
        return output;
    }

    public void print(){
        System.out.println(this.toString());
    }
}
