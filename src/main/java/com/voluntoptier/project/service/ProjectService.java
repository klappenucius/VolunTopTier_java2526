package com.voluntoptier.project.service;

import com.voluntoptier.project.entities.Address;
import com.voluntoptier.project.entities.Change;
import com.voluntoptier.project.entities.Project;
import com.voluntoptier.project.repository.ProjectCrud;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ProjectService {
    private static final String ENTITY_TYPE = "Project";

    private final ProjectCrud projectCrud;
    private final ChangeLogService changeLogService;
    private final AddressService addressService;

    public ProjectService(ProjectCrud projectCrud, ChangeLogService changeLogService, AddressService addressService) {
        this.projectCrud = projectCrud;
        this.changeLogService = changeLogService;
        this.addressService = addressService;
    }

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
        if (project.getVolunteersNeeded() == 0) {
            throw new IllegalArgumentException("The number of volunteers needed is required");
        }
        if (project.getVolunteersNeeded() < 0) {
            throw new IllegalArgumentException("The number of volunteers needed cannot be negative");
        }
        if (project.getTotalHours() == null) {
            throw new IllegalArgumentException("Total number of hours is required");
        }
        if (project.getTotalHours() < 0) {
            throw new IllegalArgumentException("Total number of hours cannot be negative");
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

    public Project createProject(Project project, String changedBy) {
        validate(project);

        Address resolvedAddress = addressService.isExisting(project.getAddress(), changedBy);
        project.setAddress(resolvedAddress);

        Project created = (Project) projectCrud.add(project);

        logTheChange(new Change(
                ENTITY_TYPE, created.getId(), "CREATE",
                null, null, created.toString(), changedBy));

        return created;
    }

    public Project fetchProject(int id) {
        Project project = (Project) projectCrud.getById(id);
        if (project == null) {
            throw new NoSuchElementException("No project found with id: " + id);
        }
        return project;
    }

    public Project updateProject(Project incomingProject, String changedBy) {
        validate(incomingProject);

        Project existingProject = fetchProject(incomingProject.getId());

        // compare each value between the existingProject and the incomingProject - if there is a change >
        // save it as 1 change in a list of changes

        List<Change> changes = new ArrayList<>();;

        if(!existingProject.getName().equals(incomingProject.getName())) {
            changes.add(new Change(ENTITY_TYPE, incomingProject.getId(), "UPDATE", "name", existingProject.getName(), incomingProject.getName(), changedBy));
        }

        Address existingAddress = existingProject.getAddress();
        Address incomingAddress = incomingProject.getAddress();
        if(existingAddress == null || !existingAddress.equals(incomingAddress)) {
            Address newAddress = addressService.isExisting(incomingAddress, changedBy);
            incomingProject.setAddress(newAddress);
            changes.add(new Change(ENTITY_TYPE, incomingProject.getId(), "UPDATE", "address",
                    existingAddress == null ? null : existingAddress.toString(),
                    newAddress.toString(), changedBy));
        }

        if(!existingProject.getTotalHours().equals(incomingProject.getTotalHours())) {
            changes.add(new Change(ENTITY_TYPE, incomingProject.getId(), "UPDATE", "total hours", existingProject.getTotalHours().toString(), incomingProject.getTotalHours().toString(), changedBy));
        }
        if(!existingProject.getHoursWorked().equals(incomingProject.getHoursWorked())) {
            changes.add(new Change(ENTITY_TYPE, incomingProject.getId(), "UPDATE", "hours worked", existingProject.getHoursWorked().toString(), incomingProject.getHoursWorked().toString(), changedBy));
        }
        if(!existingProject.getStartDate().equals(incomingProject.getStartDate())) {
            changes.add(new Change(ENTITY_TYPE, incomingProject.getId(), "UPDATE", "start date", existingProject.getStartDate().toString(), incomingProject.getStartDate().toString(), changedBy));
        }
        if(!existingProject.getEndDate().equals(incomingProject.getEndDate())) {
            changes.add(new Change(ENTITY_TYPE, incomingProject.getId(), "UPDATE", "end date", existingProject.getEndDate().toString(), incomingProject.getEndDate().toString(), changedBy));
        }
        if(!(existingProject.getVolunteersNeeded() == incomingProject.getVolunteersNeeded())) {
            changes.add(new Change(ENTITY_TYPE, incomingProject.getId(), "UPDATE", "volunteers needed", String.valueOf(existingProject.getVolunteersNeeded()), String.valueOf(incomingProject.getVolunteersNeeded()), changedBy));
        }
        projectCrud.update(incomingProject);

        for (Change change : changes) {
            logTheChange(change);
        }

        return incomingProject;
    }

    public boolean deleteProject(int id, String changedBy) {
        Project existing = fetchProject(id);

        boolean deleted = projectCrud.delete(id);

        if (deleted) {
            logTheChange(new Change(
                    ENTITY_TYPE, id, "DELETE",
                    null, existing.toString(), null, changedBy));
        }

        return deleted;
    }

}
