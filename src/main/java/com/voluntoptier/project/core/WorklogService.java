package com.voluntoptier.project.core;

import com.voluntoptier.project.entities.*;
import com.voluntoptier.project.exceptions.*;
import java.util.*;

/**
 * Coordinates Worklog operations: authorization + modifications + validation.
 */
public final class WorklogService {

    private WorklogService() {}

    /**
     * Update an existing worklog's hours for a target user & project.
     */
    public static void updateHours(
            User actor,
            User target,
            int projectId,
            int newHours
    ) throws ImpossibleInputDataException, ObjectNotFoundException {

        Objects.requireNonNull(actor, "actor");
        Objects.requireNonNull(target, "target");

        AuthorizationHelper.ensureEditRights(actor, target);

        Worklog wl = new Pipeline<>(target.getWorklogs())
                .findFirst(w -> w != null && w.getProject() != null && w.getProject().getId() == projectId)
                .orElseThrow(() ->
                        new ObjectNotFoundException("Worklog with project ID " + projectId + " not found for user " + target.getId())
                );

        wl.setHoursWorked(newHours);
        new WorklogWrapper(wl).validate();
    }

    /**
     * Create a worklog if it doesn't exist; otherwise update it. Useful for GUI "Save".
     */
    public static void upsertWorklog(
            User actor,
            User target,
            Project project,
            int hours
    ) throws ImpossibleInputDataException {

        Objects.requireNonNull(actor, "actor");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(project, "project");

        AuthorizationHelper.ensureEditRights(actor, target);

        LinkedList<Worklog> logs = target.getWorklogs();
        if (logs == null) {
            logs = new LinkedList<>();
            target.setWorklogs(logs);
        }

        Worklog existing = new Pipeline<>(logs)
                .findFirst(w -> w != null && w.getProject() != null && w.getProject().equals(project))
                .orElse(null);

        if (existing == null) {
            Worklog created = new Worklog(project, hours);
            logs.add(created);
            new WorklogWrapper(created).validate();
        } else {
            existing.setHoursWorked(hours);
            new WorklogWrapper(existing).validate();
        }
    }

    /**
     * Return all invalid worklogs for a user (useful for audits / UI warnings).
     */
    public static List<Worklog> findInvalidWorklogs(User user) {
        Objects.requireNonNull(user, "user");

        return new Pipeline<>(user.getWorklogs())
                .filter(w -> w != null && !new WorklogWrapper(w).isValid())
                .toList();
    }
}
