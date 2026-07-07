package com.voluntoptier.project.core;

import com.voluntoptier.project.entities.Worklog;
import com.voluntoptier.project.entities.Project;
import com.voluntoptier.project.exceptions.ImpossibleInputDataException;

/**
 * Business rules for ONE Worklog.
 */
public final class WorklogWrapper extends Wrapper<Worklog> {

    public WorklogWrapper(Worklog worklog) {
        super(worklog);
    }

    @Override
    public void validate() throws ImpossibleInputDataException {
        Worklog w = entity;

        if (w.getProject() == null) {
            throw new ImpossibleInputDataException("Worklog must reference a project.");
        }

        Project p = w.getProject();

        if (p.getDurationHours() <= 0) {
            throw new ImpossibleInputDataException("Project duration must be positive.");
        }

        if (w.getHoursWorked() < 0) {
            throw new ImpossibleInputDataException("Worklog hours cannot be negative.");
        }

        if (w.getHoursWorked() > p.getDurationHours()) {
            throw new ImpossibleInputDataException(
                    "Worklog hours (" + w.getHoursWorked() + ") exceed project duration (" + p.getDurationHours() + ")."
            );
        }
    }

    public boolean exceedsProjectDuration() {
        return entity.getHoursWorked() > entity.getProject().getDurationHours();
    }

    public int remainingHours() {
        return entity.getProject().getDurationHours() - entity.getHoursWorked();
    }
}
