package com.voluntoptier.project.entities;

import java.time.LocalDateTime;

public class IncentiveAssignment extends DBitem{
    protected Incentive incentive;
    protected Project project;
    protected LocalDateTime assignmentDateTime;
    protected User assignedBy;
    protected int hoursNeeded;

    public IncentiveAssignment(String id, Incentive incentive, Project project, int hoursNeeded) {
        super(id);
        this.incentive = incentive;
        this.project = project;
        this.assignmentDateTime = LocalDateTime.now();
        //this.assignedBy
        this.hoursNeeded = hoursNeeded;
    }

    public Incentive getIncentive() {
        return incentive;
    }

    public void setIncentive(Incentive incentive) {
        this.incentive = incentive;
    }

    public LocalDateTime getAssignmentDateTime() {
        return assignmentDateTime;
    }

    public void setAssignmentDateTime(LocalDateTime assignmentDateTime) {
        this.assignmentDateTime = assignmentDateTime;
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
