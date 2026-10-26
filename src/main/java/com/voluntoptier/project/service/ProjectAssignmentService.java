package com.voluntoptier.project.service;

import com.voluntoptier.project.entities.Change;
import com.voluntoptier.project.entities.HoursWorked;
import com.voluntoptier.project.entities.Project;
import com.voluntoptier.project.entities.ProjectAssignment;
import com.voluntoptier.project.entities.User;
import com.voluntoptier.project.repository.ProjectAssignmentCrud;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Business rules for assigning volunteers to projects and tracking their hours.
 */
@Service
public class ProjectAssignmentService {
    private static final String ENTITY_TYPE = "ProjectAssignment";

    private final ProjectAssignmentCrud projectAssignmentCrud;
    private final ChangeLogService changeLogService;
    private final ProjectService projectService;
    private final UserService userService;

    /** All dependencies are injected by Spring. */
    public ProjectAssignmentService(ProjectAssignmentCrud projectAssignmentCrud, ChangeLogService changeLogService,
                                    ProjectService projectService, UserService userService) {
        this.projectAssignmentCrud = projectAssignmentCrud;
        this.changeLogService = changeLogService;
        this.projectService = projectService;
        this.userService = userService;
    }

    private void validate(ProjectAssignment pa) {
        if (pa.getDate() == null || pa.getTime() == null) {
            throw new IllegalArgumentException("Project assignment date and time are required");
        }
        if (pa.getAssignedby() == null || pa.getProject() == null || pa.getUser() == null) {
            throw new IllegalArgumentException("Project, volunteer and assigning user are required");
        }
        userService.validate(pa.getAssignedby());
        projectService.validate(pa.getProject());
        userService.validate(pa.getUser());
        if (pa.getExpectedHours() <= 0) {
            throw new IllegalArgumentException("Expected hours must be positive");
        }
        if (pa.getHoursWorked() == null) {
            pa.setHoursWorked(new HoursWorked(0, 0));
        }
        if (pa.getHoursWorked().approvedHours() > pa.getExpectedHours()) {
            throw new IllegalArgumentException("Hours worked cannot exceed expected hours");
        }
    }

    /**
     * @return the assignment of this volunteer to this project, or {@code null} if there is none
     */
    public ProjectAssignment fetchProjectAssignment(User user, Project project) {
        return projectAssignmentCrud.fetchByUserAndProject(user, project);
    }

    /** @return all assignments of the volunteer with this id */
    public List<ProjectAssignment> fetchProjectsAssignedToUser(int userId) {
        return projectAssignmentCrud.getByUserId(userId);
    }

    /** @throws NoSuchElementException if there is no assignment with this id */
    public ProjectAssignment fetchProjectAssignmentById(int id) {
        ProjectAssignment pa = (ProjectAssignment) projectAssignmentCrud.getById(id);
        if (pa == null) {
            throw new NoSuchElementException("No project assignment found with id: " + id);
        }
        return pa;
    }

    /** @return approved and pending hours summed over all assignments of the volunteer with this id */
    public HoursWorked getTotalHoursForUser(int userId) {
        return projectAssignmentCrud.getTotalHoursForUser(userId);
    }

    /**
     * Moves hours between "pending" and "approved" and saves the assignment.
     *
     * @param projectAssignment the assignment to change
     * @param pendingDelta      added to the pending hours (negative to subtract)
     * @param approvedDelta     added to the approved hours (negative to subtract)
     */
    public void adjustHours(ProjectAssignment projectAssignment, int pendingDelta, int approvedDelta) {
        HoursWorked current = projectAssignment.getHoursWorked();
        int newPending = current.pendingApproval() + pendingDelta;
        int newApproved = current.approvedHours() + approvedDelta;

        if (newPending < 0 || newApproved < 0) {
            throw new IllegalArgumentException(
                    "Hours cannot go negative for assignment id " + projectAssignment.getId());
        }
        if (newApproved > projectAssignment.getExpectedHours()) {
            throw new IllegalArgumentException(
                    "Approved hours would exceed expected hours for assignment id " + projectAssignment.getId());
        }

        projectAssignment.setHoursWorked(new HoursWorked(newApproved, newPending));
        projectAssignmentCrud.update(projectAssignment);
    }

    /**
     * Saves the new values and records one change per field that differs.
     *
     * @param incoming  the assignment with its new values (and its existing id)
     * @param changedBy username of the person making the change
     * @return the updated assignment
     */
    public ProjectAssignment updateProjectAssignment(ProjectAssignment incoming, String changedBy) {
        validate(incoming);

        ProjectAssignment existing = fetchProjectAssignmentById(incoming.getId());
        int id = incoming.getId();

        List<Change> changes = new ArrayList<>();
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "date", existing.getDate(), incoming.getDate(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "time", existing.getTime(), incoming.getTime(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "assignedBy",
                existing.getAssignedby().getUsername(), incoming.getAssignedby().getUsername(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "user",
                existing.getUser().getUsername(), incoming.getUser().getUsername(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "project",
                existing.getProject().getId(), incoming.getProject().getId(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "expected hours",
                existing.getExpectedHours(), incoming.getExpectedHours(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "hours worked",
                existing.getHoursWorked(), incoming.getHoursWorked(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "active",
                existing.isActive(), incoming.isActive(), changedBy);

        projectAssignmentCrud.update(incoming);
        changes.forEach(changeLogService::logChangeSafely);

        return incoming;
    }

    /**
     * Assigns a volunteer to a project, or re-activates their earlier assignment to it.
     *
     * @param projectAssignment the new assignment
     * @param changedBy         username of the person making the change
     * @return the saved assignment
     */
    public ProjectAssignment assignUser(ProjectAssignment projectAssignment, String changedBy) {
        validate(projectAssignment);

        ProjectAssignment existing = projectAssignmentCrud.fetchByUserAndProject(
                projectAssignment.getUser(), projectAssignment.getProject());

        if (existing != null) {
            existing.setActive(true);
            projectAssignmentCrud.update(existing);
            changeLogService.logChangeSafely(new Change(
                    ENTITY_TYPE, existing.getId(), "ACTIVATE", null, null, existing.toString(), changedBy));
            return existing;
        }

        ProjectAssignment created = (ProjectAssignment) projectAssignmentCrud.add(projectAssignment);
        changeLogService.logChangeSafely(new Change(
                ENTITY_TYPE, created.getId(), "CREATE", null, null, created.toString(), changedBy));
        return created;
    }

    /** Deactivates an assignment; its hours are kept. */
    public ProjectAssignment removeUser(int id, String changedBy) {
        ProjectAssignment existing = fetchProjectAssignmentById(id);

        existing.setActive(false);
        projectAssignmentCrud.update(existing);

        changeLogService.logChangeSafely(new Change(
                ENTITY_TYPE, id, "DEACTIVATE", null, existing.toString(), null, changedBy));
        return existing;
    }

    /** Deletes an assignment for good; returns {@code true} if it was deleted. */
    public boolean hardDelete(int id, String changedBy) {
        ProjectAssignment existing = fetchProjectAssignmentById(id);

        boolean deleted = projectAssignmentCrud.delete(id);

        if (deleted) {
            changeLogService.logChangeSafely(new Change(
                    ENTITY_TYPE, id, "DELETE", null, existing.toString(), null, changedBy));
        }
        return deleted;
    }
}
