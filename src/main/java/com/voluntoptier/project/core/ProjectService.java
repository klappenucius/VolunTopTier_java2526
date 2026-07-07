package com.voluntoptier.project.core;

import com.voluntoptier.project.entities.Project;
import com.voluntoptier.project.entities.Role;
import com.voluntoptier.project.entities.User;
import com.voluntoptier.project.exceptions.ImpossibleInputDataException;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Coordinates Project operations: authorization + modifications + validation.
 */
public final class ProjectService {

    private ProjectService() {}

    public static void updateName(User actor, Project project, String newName)
            throws ImpossibleInputDataException {

        AuthorizationHelper.requireAdmin(actor);
        Objects.requireNonNull(project, "project");

        project.setName(newName);
        new ProjectWrapper(project).validate();
    }

    public static void updateLocation(User actor, Project project, String newLocation)
            throws ImpossibleInputDataException {

        AuthorizationHelper.requireAdmin(actor);
        Objects.requireNonNull(project, "project");

        project.setLocation(newLocation);
        new ProjectWrapper(project).validate();
    }

    public static void updateDates(User actor, Project project, LocalDate start, LocalDate end)
            throws ImpossibleInputDataException {

        AuthorizationHelper.requireAdmin(actor);
        Objects.requireNonNull(project, "project");

        project.setStartDate(start);
        project.setEndDate(end);
        new ProjectWrapper(project).validate();
    }

    public static void updateDuration(User actor, Project project, int newDurationHours)
            throws ImpossibleInputDataException {

        AuthorizationHelper.requireAdmin(actor);
        Objects.requireNonNull(project, "project");

        project.setDurationHours(newDurationHours);
        new ProjectWrapper(project).validate();
    }

    public static void updateId(User actor, Project project, int newId)
            throws ImpossibleInputDataException {

        AuthorizationHelper.requireAdmin(actor);
        Objects.requireNonNull(project, "project");

        project.setId(newId);
        new ProjectWrapper(project).validate();
    }
}
