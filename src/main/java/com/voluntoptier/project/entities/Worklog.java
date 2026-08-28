package com.voluntoptier.project.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Worklog extends DBitem {
    private ProjectAssignment projectAssignment;
    private LocalDate date;
    private int hours;
    private String description;
    private WorklogStatus status;
    private LocalDateTime submittedAt;

    public Worklog(int id, ProjectAssignment projectAssignment, LocalDate date, int hours, String description,
                   WorklogStatus status, LocalDateTime submittedAt) {
        super(id);
        this.projectAssignment = projectAssignment;
        this.date = date;
        this.hours = hours;
        this.description = description;
        this.status = status;
        this.submittedAt = submittedAt;
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

    public void print() {
        System.out.println(this.toString());
    }
}
