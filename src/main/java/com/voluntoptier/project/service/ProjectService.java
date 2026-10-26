package com.voluntoptier.project.service;

import com.voluntoptier.project.entities.Address;
import com.voluntoptier.project.entities.Change;
import com.voluntoptier.project.entities.Project;
import com.voluntoptier.project.repository.ProjectCrud;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Business rules for projects.
 */
@Service
public class ProjectService {
    private static final String ENTITY_TYPE = "Project";

    private final ProjectCrud projectCrud;
    private final ChangeLogService changeLogService;
    private final AddressService addressService;

    /**
     * @param projectCrud      data access for projects
     * @param changeLogService records every change
     * @param addressService   resolves the project's address
     */
    public ProjectService(ProjectCrud projectCrud, ChangeLogService changeLogService, AddressService addressService) {
        this.projectCrud = projectCrud;
        this.changeLogService = changeLogService;
        this.addressService = addressService;
    }

    /**
     * @param project the project to check
     * @throws IllegalArgumentException if a required value is missing or impossible
     */
    public void validate(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }
        if (project.getName() == null || project.getName().isBlank()) {
            throw new IllegalArgumentException("Project name is required");
        }
        if (project.getStartDate() == null || project.getEndDate() == null) {
            throw new IllegalArgumentException("Start and end dates are required");
        }
        if (project.getEndDate().isBefore(project.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        if (project.getVolunteersNeeded() <= 0) {
            throw new IllegalArgumentException("The number of volunteers needed must be positive");
        }
        if (project.getTotalHours() == null || project.getTotalHours() <= 0) {
            throw new IllegalArgumentException("Total number of hours must be positive");
        }
    }

    /**
     * @param project   the new project
     * @param changedBy username of the person making the change
     * @return the saved project, with its new id
     */
    public Project createProject(Project project, String changedBy) {
        validate(project);

        project.setAddress(addressService.isExisting(project.getAddress(), changedBy));

        Project created = (Project) projectCrud.add(project);

        changeLogService.logChangeSafely(new Change(
                ENTITY_TYPE, created.getId(), "CREATE",
                null, null, created.toString(), changedBy));

        return created;
    }

    /**
     * @param id the project id
     * @return the project
     * @throws NoSuchElementException if there is no project with this id
     */
    public Project fetchProject(int id) {
        Project project = (Project) projectCrud.getById(id);
        if (project == null) {
            throw new NoSuchElementException("No project found with id: " + id);
        }
        return project;
    }

    /**
     * Saves the new values and records one change per field that differs.
     *
     * @param incomingProject the project with its new values (and its existing id)
     * @param changedBy       username of the person making the change
     * @return the updated project
     */
    public Project updateProject(Project incomingProject, String changedBy) {
        validate(incomingProject);

        Project existing = fetchProject(incomingProject.getId());
        int id = incomingProject.getId();

        Address resolvedAddress = addressService.resolveAddress(
                existing.getAddress(), incomingProject.getAddress(), changedBy);
        incomingProject.setAddress(resolvedAddress);

        List<Change> changes = new ArrayList<>();
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "name",
                existing.getName(), incomingProject.getName(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "address",
                existing.getAddress(), resolvedAddress, changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "total hours",
                existing.getTotalHours(), incomingProject.getTotalHours(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "start date",
                existing.getStartDate(), incomingProject.getStartDate(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "end date",
                existing.getEndDate(), incomingProject.getEndDate(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "volunteers needed",
                existing.getVolunteersNeeded(), incomingProject.getVolunteersNeeded(), changedBy);

        projectCrud.update(incomingProject);
        changes.forEach(changeLogService::logChangeSafely);

        return incomingProject;
    }

    /**
     * @param id        the project id
     * @param changedBy username of the person making the change
     * @return {@code true} if the project was deleted
     */
    public boolean deleteProject(int id, String changedBy) {
        Project existing = fetchProject(id);

        boolean deleted = projectCrud.delete(id);

        if (deleted) {
            changeLogService.logChangeSafely(new Change(
                    ENTITY_TYPE, id, "DELETE",
                    null, existing.toString(), null, changedBy));
        }

        return deleted;
    }
}
