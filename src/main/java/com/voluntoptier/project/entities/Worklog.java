package com.voluntoptier.project.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * One entry of logged work on a project assignment, waiting for or already given approval.
 */
public class Worklog extends DBitem {
    private ProjectAssignment projectAssignment;
    private LocalDate date;
    private int hours;
    private String description;
    private WorklogStatus status;
    private LocalDateTime submittedAt;
    private String approvedBy;
    private LocalDateTime resolvedAt;

    /**
     * Creates a work log; {@code submittedAt} is set to now and can be overwritten when
     * the work log is loaded from the database.
     *
     * @param id                database id (0 for a work log not saved yet)
     * @param projectAssignment the assignment the work was done on
     * @param date              the day the work was done
     * @param hours             number of hours worked
     * @param description       optional description of the work
     * @param status            approval status
     */
    public Worklog(int id, ProjectAssignment projectAssignment, LocalDate date, int hours, String description,
                   WorklogStatus status) {
        super(id);
        this.projectAssignment = projectAssignment;
        this.date = date;
        this.hours = hours;
        this.description = description;
        this.status = status;
        this.submittedAt = LocalDateTime.now();
    }

    public ProjectAssignment getProjectAssignment() {
        return projectAssignment;
    }

    public void setProjectAssignment(ProjectAssignment projectAssignment) {
        this.projectAssignment = projectAssignment;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WorklogStatus getStatus() {
        return status;
    }

    public void setStatus(WorklogStatus status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    @Override
    public String toString() {
        return "Worklog{" +
                "id=" + getId() +
                ", assignmentId=" + (projectAssignment != null ? projectAssignment.getId() : null) +
                ", date=" + date +
                ", hours=" + hours +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public void print() {
        System.out.println(this);
    }
}