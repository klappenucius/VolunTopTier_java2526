package com.voluntoptier.project.service;

import com.voluntoptier.project.entities.Change;
import com.voluntoptier.project.entities.ProjectAssignment;
import com.voluntoptier.project.entities.Worklog;
import com.voluntoptier.project.entities.WorklogStatus;
import com.voluntoptier.project.repository.WorklogCrud;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/**
 * Business rules for logging work and approving or rejecting it.
 * Each public write method runs in one database transaction: the work log and the assignment's
 * hours are saved together, or (if anything fails) not at all.
 */
@Service
public class WorklogService {
    private static final String ENTITY_TYPE = "Worklog";

    private final WorklogCrud worklogCrud;
    private final ChangeLogService changeLogService;
    private final ProjectAssignmentService projectAssignmentService;

    /**
     * @param worklogCrud              data access for work logs
     * @param changeLogService         records every change
     * @param projectAssignmentService updates the assignment's pending and approved hours
     */
    public WorklogService(WorklogCrud worklogCrud, ChangeLogService changeLogService,
                          ProjectAssignmentService projectAssignmentService) {
        this.worklogCrud = worklogCrud;
        this.changeLogService = changeLogService;
        this.projectAssignmentService = projectAssignmentService;
    }

    private void validate(Worklog worklog) {
        if (worklog == null) {
            throw new IllegalArgumentException("Worklog cannot be null");
        }
        if (worklog.getProjectAssignment() == null) {
            throw new IllegalArgumentException("Project assignment is required");
        }
        if (worklog.getDate() == null) {
            throw new IllegalArgumentException("Date is required");
        }
        if (worklog.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date cannot be in the future");
        }
        if (worklog.getHours() <= 0) {
            throw new IllegalArgumentException("Hours must be positive");
        }
    }

    private static void requirePending(Worklog worklog) {
        if (worklog.getStatus() != WorklogStatus.PENDING) {
            throw new IllegalStateException(
                    "Work log " + worklog.getId() + " is already " + worklog.getStatus());
        }
    }

    /**
     * @param id the work log id
     * @return the work log
     * @throws NoSuchElementException if there is no work log with this id
     */
    public Worklog fetchWorkLog(int id) {
        Worklog workLog = (Worklog) worklogCrud.getById(id);
        if (workLog == null) {
            throw new NoSuchElementException("No work log found with id: " + id);
        }
        return workLog;
    }

    /**
     * Saves a new work log as PENDING and adds its hours to the assignment's pending hours.
     *
     * @param worklog     the new work log
     * @param submittedBy username of the volunteer submitting it
     * @return the saved work log, with its new id
     */
    @Transactional
    public Worklog submitWorklog(Worklog worklog, String submittedBy) {
        validate(worklog);

        ProjectAssignment projectAssignment =
                projectAssignmentService.fetchProjectAssignmentById(worklog.getProjectAssignment().getId());
        if (!projectAssignment.isActive()) {
            throw new IllegalStateException("Cannot log work on an inactive assignment");
        }
        worklog.setProjectAssignment(projectAssignment);
        worklog.setStatus(WorklogStatus.PENDING);
        worklog.setSubmittedAt(LocalDateTime.now());
        worklog.setApprovedBy(null);
        worklog.setResolvedAt(null);

        worklogCrud.add(worklog);
        projectAssignmentService.adjustHours(projectAssignment, worklog.getHours(), 0);

        changeLogService.logChangeSafely(new Change(
                ENTITY_TYPE, worklog.getId(), "CREATE", null, null, worklog.toString(), submittedBy));
        return worklog;
    }

    /**
     * Approves a pending work log and moves its hours from pending to approved.
     *
     * @param id       the work log id
     * @param approver username of the moderator approving it
     * @return the approved work log
     */
    @Transactional
    public Worklog approveWorklog(int id, String approver) {
        Worklog worklog = fetchWorkLog(id);
        requirePending(worklog);

        worklog.setStatus(WorklogStatus.APPROVED);
        worklog.setApprovedBy(approver);
        worklog.setResolvedAt(LocalDateTime.now());

        worklogCrud.update(worklog);
        projectAssignmentService.adjustHours(worklog.getProjectAssignment(), -worklog.getHours(), worklog.getHours());

        changeLogService.logChangeSafely(new Change(
                ENTITY_TYPE, id, "UPDATE", "status",
                WorklogStatus.PENDING.name(), WorklogStatus.APPROVED.name(), approver));
        return worklog;
    }

    /**
     * Rejects a pending work log: deletes it and removes its hours from the pending hours.
     *
     * @param id       the work log id
     * @param approver username of the moderator rejecting it
     * @return {@code true} if the work log was deleted
     */
    @Transactional
    public boolean rejectWorklog(int id, String approver) {
        Worklog existing = fetchWorkLog(id);
        requirePending(existing);

        boolean deleted = worklogCrud.delete(id);
        projectAssignmentService.adjustHours(existing.getProjectAssignment(), -existing.getHours(), 0);

        changeLogService.logChangeSafely(new Change(
                ENTITY_TYPE, id, "DELETE", null, existing.toString(), null, approver));
        return deleted;
    }
}
