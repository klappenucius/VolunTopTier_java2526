package com.voluntoptier.project.service;

import com.voluntoptier.project.entities.Address;
import com.voluntoptier.project.entities.Change;
import com.voluntoptier.project.entities.User;
import com.voluntoptier.project.repository.AddressCrud;
import com.voluntoptier.project.repository.UserCrud;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class AddressService {

    private static final String ENTITY_TYPE = "Address";

    private final AddressCrud addressCrud;
    private final ChangeLogService changeLogService;

    public AddressService(AddressCrud addressCrud, ChangeLogService changeLogService) {
        this.addressCrud = addressCrud;
        this.changeLogService = changeLogService;
    }

    private void validate(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        if (address.getStreet() == null || address.getStreet().isBlank()) {
            throw new IllegalArgumentException("Street is required");
        }
        if (address.getHouseNumber() == null || address.getHouseNumber().isBlank()) {
            throw new IllegalArgumentException("House number is required");
        }
        if (address.getPostalCode() == null || address.getPostalCode().isBlank()) {
            throw new IllegalArgumentException("Postal code is required");
        }
        if (address.getCity() == null || address.getCity().isBlank()) {
            throw new IllegalArgumentException("City is required");
        }
        if (address.getCountry() == null || address.getCountry().isBlank()) {
            throw new IllegalArgumentException("Country is required");
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

    public Address createAddress(Address address, String changedBy) {
        validate(address);

        Address created = (Address) addressCrud.add(address);

        logTheChange(new Change(
                ENTITY_TYPE, created.getId(), "CREATE",
                null, null, created.toString(), changedBy));

        return created;
    }

    public User fetchUser(int id) {
        User user = (User) userCrud.getById(id);
        if (user == null) {
            throw new NoSuchElementException("No user found with id: " + id);
        }
        return user;
    }

    public User updateUser(User incomingUser, String changedBy) {
        validate(incomingUser);

        User existingUser = fetchUser(incomingUser.getId());

        // compare each value between the existingProject and the incomingProject - if there is a change >
        // save it as 1 change in a list of changes

        List<Change> changes = new ArrayList<>();;

        if(!existingUser.getFirstName().equals(incomingUser.getFirstName())) {
            changes.add(new Change(ENTITY_TYPE, incomingUser.getId(), "UPDATE", "first name", existingUser.getFirstName(), incomingUser.getFirstName(), changedBy));
        }
        // calling a separate AddressService for the address
        if(!existingUser.getLastName().equals(incomingUser.getLastName())) {
            changes.add(new Change(ENTITY_TYPE, incomingUser.getId(), "UPDATE", "last name", existingUser.getFirstName(), incomingUser.getLastName(), changedBy));
        }
        if(!existingUser.getUsername().equals(incomingUser.getUsername())) {
            changes.add(new Change(ENTITY_TYPE, incomingUser.getId(), "UPDATE", "username", existingUser.getUsername(), incomingUser.getUsername(), changedBy));
        }
        if(!existingUser.getOib().equals(incomingUser.getUsername())) {
            changes.add(new Change(ENTITY_TYPE, incomingUser.getId(), "UPDATE", "oib", existingUser.getOib(), incomingUser.getOib(), changedBy));
        }
        if(!existingUser.getDateOfBirth().equals(incomingUser.getDateOfBirth())) {
            changes.add(new Change(ENTITY_TYPE, incomingUser.getId(), "UPDATE", "date of birth", existingUser.getDateOfBirth().toString(), incomingUser.getDateOfBirth().toString(), changedBy));
        }
        if(!(existingUser.getRole().name().equals(incomingUser.getRole().name()))) {
            changes.add(new Change(ENTITY_TYPE, incomingUser.getId(), "UPDATE", "role", existingUser.getRole().name(), incomingUser.getRole().name(), changedBy));
        }
        if(!(existingUser.getTotalHoursWorked() == incomingUser.getTotalHoursWorked())) {
            changes.add(new Change(ENTITY_TYPE, incomingUser.getId(), "UPDATE", "total hours worked", String.valueOf(existingUser.getTotalHoursWorked()), String.valueOf(incomingUser.getTotalHoursWorked()), changedBy));
        }
        userCrud.update(incomingUser);

        for (Change change : changes) {
            logTheChange(change);
        }

        return incomingUser;
    }

    public boolean deleteUser(int id, String changedBy) {
        User existing = fetchUser(id);

        boolean deleted = userCrud.delete(id);

        if (deleted) {
            logTheChange(new Change(
                    ENTITY_TYPE, id, "DELETE",
                    null, existing.toString(), null, changedBy));
        }

        return deleted;
    }
}
