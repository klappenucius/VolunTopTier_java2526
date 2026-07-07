package com.voluntoptier.project.entities;

/**
 * Represents a record of work completed by a user on a particular project.
 * Each worklog entry associates a {@link Project} with the number of
 * hours worked.
 */
public class Worklog {
    private Project project;
    private int hoursWorked;

    /**
     * Constructs a new {@code Worklog} with the specified project and hours.
     *
     * @param project the {@link Project} associated with the worklog
     * @param hoursWorked the number of hours worked
     */
    public Worklog(Project project, int hoursWorked) {
        this.project = project;
        this.hoursWorked = hoursWorked;
    }

    public Worklog() {}

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public int getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(int hoursWorked) { this.hoursWorked = hoursWorked; }

    @Override
    public String toString() {
        return "Worklog{" +
                "project=" + project.getName() +
                ", hoursWorked=" + hoursWorked +
                '}';
    }

    @Override
    public boolean equals (Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Worklog worklog = (Worklog) object;
        if (hoursWorked != worklog.hoursWorked) return false;
        if (project != null) {
            return project.equals(worklog.project);
        } else {
            return worklog.project == null;
        }
    }

    @Override
    public int hashCode() {
        int result = hoursWorked; // start with the primitive field

        if (project != null) {
            result = 31 * result + project.hashCode();
        } else {
            result = 31 * result + 0; // same as adding nothing
        }
        return result;
    }
}
