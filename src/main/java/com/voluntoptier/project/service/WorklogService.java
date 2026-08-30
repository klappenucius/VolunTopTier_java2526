package com.voluntoptier.project.service;

import com.voluntoptier.project.entities.Change;
import com.voluntoptier.project.entities.ProjectAssignment;
import com.voluntoptier.project.entities.Worklog;
import com.voluntoptier.project.entities.WorklogStatus;
import com.voluntoptier.project.repository.WorklogCrud;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.NoSuchElementException;

public class WorklogService {
    private static final String ENTITY_TYPE = "Worklog";

    private final Connection connection;
    private final WorklogCrud worklogCrud;
    private final ChangeLogService changeLogService;
    private final ProjectAssignmentService projectAssignmentService;

    public WorklogService(Connection connection, WorklogCrud worklogCrud,
                          ChangeLogService changeLogService, ProjectAssignmentService projectAssignmentService) {
        this.connection = connection;
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

    private void logTheChange(Change change) {
        try {
            changeLogService.logChange(change);
        } catch (RuntimeException e) {
            System.err.println("Failed to write change log entry for "
                    + change.getEntityType() + " id=" + change.getEntityId()
                    + ": " + e.getMessage());
        }
    }

    public Worklog fetchWorkLog(int id) {
        Worklog workLog = (Worklog) worklogCrud.getById(id);
        if (workLog == null) {
            throw new NoSuchElementException("No work log found with id: " + id);
        }
        return workLog;
    }

    public Worklog submitWorklog (Worklog worklog, String submittedBy) {
        validate(worklog);

        ProjectAssignment projectAssignment = worklog.getProjectAssignment();

        try {
            connection.setAutoCommit(false);

            worklogCrud.add(worklog);
            projectAssignmentService.adjustHours(projectAssignment, worklog.getHours(), 0);

            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback also failed: " + rollbackEx.getMessage());
            }

            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException("Failed to submit work log", e);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit state", e);
            }
        }
        logTheChange(new Change(
                ENTITY_TYPE, worklog.getId(), "CREATE",
                null, null, worklog.toString(), submittedBy));

        return worklog;

    }

    public Worklog approveWorklog (int id, String approver){
        Worklog worklog = (Worklog) worklogCrud.getById(id);

        worklog.setStatus(WorklogStatus.APPROVED);
        ProjectAssignment projectAssignment = worklog.getProjectAssignment();

        try {
            connection.setAutoCommit(false);

            worklogCrud.update(worklog);
            projectAssignmentService.adjustHours(projectAssignment, -worklog.getHours(), worklog.getHours());

            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback also failed: " + rollbackEx.getMessage());
            }

            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException("Failed to submit work log", e);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore auto-commit state", e);
            }
        }

        logTheChange(new Change(
                ENTITY_TYPE, worklog.getId(), "CREATE",
                null, null, worklog.toString(), approver));

        return worklog;
    }

    public boolean rejectWorklog (int id, String approver){

        Worklog existing = fetchWorkLog(id);
        ProjectAssignment projectAssignment = existing.getProjectAssignment();
        boolean deleted = false;

        try {
            connection.setAutoCommit(false);

            deleted = worklogCrud.delete(id);
            projectAssignmentService.adjustHours(projectAssignment, -existing.getHours(), 0);

            connection.commit();

            logTheChange(new Change(
                    ENTITY_TYPE, id, "DELETE",
                    null, existing.toString(), null, approver));

        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception e2) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback also failed: " + rollbackEx.getMessage());
                }

                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new RuntimeException("Failed to submit work log", e);
                }
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e3) {
                    throw new RuntimeException("Failed to restore auto-commit state", e);
                }
            }
        }

        return deleted;
    }


}
