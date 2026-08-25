package com.voluntoptier.project.service;

import com.voluntoptier.project.entities.Address;
import com.voluntoptier.project.entities.Change;
import com.voluntoptier.project.entities.User;
import com.voluntoptier.project.repository.UserCrud;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class UserService {
    private static final String ENTITY_TYPE = "User";

    private final UserCrud userCrud;
    private final ChangeLogService changeLogService;
    private final AddressService addressService;

    public UserService(UserCrud userCrud, ChangeLogService changeLogService, AddressService addressService) {
        this.userCrud = userCrud;
        this.changeLogService = changeLogService;
        this.addressService = addressService;
    }

    public void validate(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (user.getLastName() == null || user.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("username is required");
        }
        if (user.getOib() == null || user.getOib().isBlank()) {
            throw new IllegalArgumentException("Oib is required");
        }
        if (user.getDateOfBirth() == null) {
            throw new IllegalArgumentException("Date of birth is required");
        }
        if(user.getAddress() == null) {
            throw new IllegalArgumentException("Address is required");
        }
        //addressService.validate(user.getAddress());
        //if (user.getEmail() == null || user.getEmail().isBlank()) {
            //throw new IllegalArgumentException("Email is required");
        //}
        if (user.getTotalHoursWorked().pendingApproval() < 0 || user.getTotalHoursWorked().approvedHours() < 0) {
            throw new IllegalArgumentException("Booked hours cannot be negative");
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

    public User createUser(User user, String changedBy) {
        validate(user);

        Address resolvedAddress = addressService.isExisting(user.getAddress(), changedBy);
        user.setAddress(resolvedAddress);

        User created = (User) userCrud.add(user);

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

    public User fetchByUsername(String username) {
        User user = (User) userCrud.fetchByUsername(username);
        if (user == null) {
            throw new NoSuchElementException("No user found with username: " + username);
        }

        return user;
    }

    public User updateUser(User incomingUser, String changedBy) {
        validate(incomingUser);

        User existingUser = fetchUser(incomingUser.getId());

        // compare each value between the existingProject and the incomingProject - if there is a change >
        // save it as 1 change in a list of changes

        List<Change> changes = new ArrayList<>();

        if(!existingUser.getFirstName().equals(incomingUser.getFirstName())) {
            changes.add(new Change(ENTITY_TYPE, incomingUser.getId(), "UPDATE", "first name", existingUser.getFirstName(), incomingUser.getFirstName(), changedBy));
        }
        //
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

        Address existingAddress = existingUser.getAddress();
        Address incomingAddress = incomingUser.getAddress();
        if(existingAddress == null || !existingAddress.equals(incomingAddress)) {
            Address newAddress = addressService.isExisting(incomingAddress, changedBy);
            incomingUser.setAddress(newAddress);
            changes.add(new Change(ENTITY_TYPE, incomingUser.getId(), "UPDATE", "address",
                    existingAddress == null ? null : existingAddress.toString(),
                    newAddress.toString(), changedBy));
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
