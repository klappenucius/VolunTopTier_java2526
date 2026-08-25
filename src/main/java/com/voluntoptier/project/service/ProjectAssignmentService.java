package com.voluntoptier.project.service;

import com.voluntoptier.project.entities.*;
import com.voluntoptier.project.repository.ProjectAssignmentCrud;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ProjectAssignmentService {
    private static final String ENTITY_TYPE = "ProjectAssignment";

    private final ProjectAssignmentCrud projectAssignmentCrud;
    private final ChangeLogService changeLogService;
    private final ProjectService projectService;
    private final UserService userService;

    public ProjectAssignmentService(ProjectAssignmentCrud projectAssignmentCrud, ChangeLogService changeLogService, ProjectService projectService, UserService userService) {
        this.projectAssignmentCrud = projectAssignmentCrud;
        this.changeLogService = changeLogService;
        this.projectService = projectService;
        this.userService = userService;
    }

    private void validate(ProjectAssignment projectAssignment) {
        if(projectAssignment.getDate() == null) {
            throw new IllegalArgumentException("Project assignment date cannot be null");
        }
        if(projectAssignment.getTime() == null) {
            throw new IllegalArgumentException("Project assignment time cannot be null");
        }
        if(projectAssignment.getAssignedby() == null) {
            throw new IllegalArgumentException("AssignedBy cannot be null");
        } else {
        userService.validate(projectAssignment.getAssignedby());
        }
        if(projectAssignment.getProject() == null) {
            throw new IllegalArgumentException("Project cannot be null");
        } else {
            projectService.validate(projectAssignment.getProject());
        }
        if(projectAssignment.getUser() == null) {
            throw new IllegalArgumentException("User cannot be null");
        } else {
            userService.validate(projectAssignment.getUser());
        }
        if(projectAssignment.getExpectedHours() == 0) {
            throw new IllegalArgumentException("Expected hours cannot be null");
        } else {
            if(projectAssignment.getExpectedHours() < projectAssignment.getHoursWorked().approvedHours()) {
                throw new IllegalArgumentException("Hours worked cannot exceed expected hours");
            }
        }

    }

    private void logTheChange(Change change) {
        try {
            changeLogService.logChange(change);
        } catch (RuntimeException e) {
            System.err.println("Failed to write change log entry for "
                    + change.getEntityType() + " id=" + change.getEntityId()
                    + ": " + e.getMessage());
        }
    }

    public ProjectAssignment fetchProjectAssignment (User user, Project project) {
        return projectAssignmentCrud.fetchByUserAndProject(user, project);
    }

    public ProjectAssignment updateProjectAssignment(ProjectAssignment incomingProjectAssignment, String changedBy) {
        validate(incomingProjectAssignment);

        ProjectAssignment existing = (ProjectAssignment) projectAssignmentCrud.getById(incomingProjectAssignment.getId());
        if (existing == null) {
            throw new NoSuchElementException("No project assignment found with id: " + incomingProjectAssignment.getId());
        }

        List<Change> changes = new ArrayList<>();

        if(!existing.getDate().equals(incomingProjectAssignment.getDate())){
            changes.add(new Change(ENTITY_TYPE, incomingProjectAssignment.getId(), "UPDATE", "date", existing.getDate().toString(), incomingProjectAssignment.getDate().toString(), changedBy));
        }
        if(!existing.getTime().equals(incomingProjectAssignment.getTime())){
            changes.add(new Change(ENTITY_TYPE, incomingProjectAssignment.getId(), "UPDATE", "time", existing.getTime().toString(), incomingProjectAssignment.getTime().toString(), changedBy));
        }
        if(!existing.getAssignedby().equals(incomingProjectAssignment.getAssignedby())){
            changes.add(new Change(ENTITY_TYPE, incomingProjectAssignment.getId(), "UPDATE", "assignedBy", existing.getAssignedby().getUsername(),
                    incomingProjectAssignment.getAssignedby().getUsername(), changedBy));
        }
        if(!existing.getUser().equals(incomingProjectAssignment.getUser())){
            changes.add(new Change(ENTITY_TYPE, incomingProjectAssignment.getId(), "UPDATE", "user", existing.getUser().getUsername(),
                    incomingProjectAssignment.getUser().getUsername(), changedBy));
        }
        if(existing.getExpectedHours() == incomingProjectAssignment.getExpectedHours()){
            changes.add(new Change(ENTITY_TYPE, incomingProjectAssignment.getId(), "UPDATE", "expected hours", Integer.toString(existing.getExpectedHours()),
                    Integer.toString(incomingProjectAssignment.getExpectedHours()), changedBy));
        }
        if(!existing.getHoursWorked().equals(incomingProjectAssignment.getHoursWorked())){
            changes.add(new Change(ENTITY_TYPE, incomingProjectAssignment.getId(), "UPDATE", "hours worked", existing.getHoursWorked().toString(),
                    incomingProjectAssignment.getHoursWorked().toString(), changedBy));
        }
        if(existing.isActive() == incomingProjectAssignment.isActive()){
            changes.add(new Change(ENTITY_TYPE, incomingProjectAssignment.getId(), "UPDATE", "status", Boolean.toString(existing.isActive()),
                    Boolean.toString(incomingProjectAssignment.isActive()), changedBy));
        }

        projectAssignmentCrud.update(incomingProjectAssignment);

        for (Change change : changes) {
            logTheChange(change);
        }

        return incomingProjectAssignment;

    }

    public ProjectAssignment assignUser(ProjectAssignment projectAssignment, String changedBy) {
        validate(projectAssignment);

        ProjectAssignment existing = projectAssignmentCrud.fetchByUserAndProject(projectAssignment.getUser(), projectAssignment.getProject());
        if (!(existing == null)) {
            existing.setActive(true);
            projectAssignmentCrud.update(existing);

            logTheChange(new Change(
                    ENTITY_TYPE, existing.getId(), "ACTIVATE",
                    null, null, existing.toString(), changedBy));

            return existing;

        } else {
            ProjectAssignment created = (ProjectAssignment) projectAssignmentCrud.add(projectAssignment);

            logTheChange(new Change(
                    ENTITY_TYPE, created.getId(), "CREATE",
                    null, null, created.toString(), changedBy));

            return created;
        }
    }

    public ProjectAssignment removeUser(int id, String changedBy) {
        ProjectAssignment existing = (ProjectAssignment) projectAssignmentCrud.getById(id);
        if (existing == null) {
            throw new NoSuchElementException("No project assignment found with id: " + id);
        }

        existing.setActive(false);
        projectAssignmentCrud.update(existing);

            logTheChange(new Change(
                    ENTITY_TYPE, id, "DEACTIVATE",
                    null, existing.toString(), null, changedBy));

        return existing;
    }

    public boolean hardDelete(int id, String changedBy) {
        ProjectAssignment existing = (ProjectAssignment) projectAssignmentCrud.getById(id);
        if (existing == null) {
            throw new NoSuchElementException("No project assignment found with id: " + id);
        }

        boolean deleted = projectAssignmentCrud.delete(id);

        if (deleted) {
            logTheChange(new Change(
                    ENTITY_TYPE, id, "DELETE",
                    null, existing.toString(), null, changedBy));
        }

        return deleted;
    }
}
