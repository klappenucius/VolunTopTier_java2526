package com.voluntoptier.project.core;

import com.voluntoptier.project.entities.Project;
import com.voluntoptier.project.exceptions.ImpossibleInputDataException;

import java.time.LocalDate;

/**
 * Business rules for ONE Project.
 */
public final class ProjectWrapper extends Wrapper<Project> {

    public ProjectWrapper(Project project) {
        super(project);
    }

    @Override
    public void validate() throws ImpossibleInputDataException {
        Project p = entity;

        if (p.getId() <= 0) {
            throw new ImpossibleInputDataException("Project ID must be positive.");
        }

        if (p.getName() == null || p.getName().isBlank()) {
            throw new ImpossibleInputDataException("Project name cannot be empty.");
        }

        if (p.getDurationHours() <= 0) {
            throw new ImpossibleInputDataException("Project duration must be positive.");
        }

        LocalDate start = p.getStartDate();
        LocalDate end = p.getEndDate();

        // Only validate date ordering if both are present
        if (start != null && end != null && start.isAfter(end)) {
            throw new ImpossibleInputDataException("Project start date cannot be after end date.");
        }
    }

    public boolean hasValidDates() {
        LocalDate start = entity.getStartDate();
        LocalDate end = entity.getEndDate();
        return start == null || end == null || !start.isAfter(end);
    }

    public boolean hasValidDuration() {
        return entity.getDurationHours() > 0;
    }
}
