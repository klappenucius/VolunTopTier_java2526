package com.voluntoptier.project.service;

import com.voluntoptier.project.entities.*;
import com.voluntoptier.project.repository.ProjectAssignmentCrud;

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

    }

    public ProjectAssignment assignUser(ProjectAssignment projectAssignment, String changedBy) {
        validate(projectAssignment);

        ProjectAssignment created = (ProjectAssignment) projectAssignmentCrud.add(projectAssignment);

        logTheChange(new Change(
                ENTITY_TYPE, created.getId(), "CREATE",
                null, null, created.toString(), changedBy));

        return created;
    }


}
