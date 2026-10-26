package com.voluntoptier.project.service;

import com.voluntoptier.project.entities.Address;
import com.voluntoptier.project.entities.Change;
import com.voluntoptier.project.entities.User;
import com.voluntoptier.project.repository.UserCrud;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Business rules for users.
 */
@Service
public class UserService {
    private static final String ENTITY_TYPE = "User";

    private final UserCrud userCrud;
    private final ChangeLogService changeLogService;
    private final AddressService addressService;

    /**
     * @param userCrud         data access for users
     * @param changeLogService records every change
     * @param addressService   resolves the user's address
     */
    public UserService(UserCrud userCrud, ChangeLogService changeLogService, AddressService addressService) {
        this.userCrud = userCrud;
        this.changeLogService = changeLogService;
        this.addressService = addressService;
    }

    /**
     * @param user the user to check
     * @throws IllegalArgumentException if a required value is missing
     */
    public void validate(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        requireText(user.getFirstName(), "First name");
        requireText(user.getLastName(), "Last name");
        requireText(user.getUsername(), "Username");
        requireText(user.getOib(), "OIB");
        requireText(user.getEmail(), "Email");
        if (user.getDateOfBirth() == null) {
            throw new IllegalArgumentException("Date of birth is required");
        }
        if (user.getAddress() == null) {
            throw new IllegalArgumentException("Address is required");
        }
        if (user.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    /**
     * @param user      the new user
     * @param changedBy username of the person making the change
     * @return the saved user, with its new id
     */
    public User createUser(User user, String changedBy) {
        validate(user);

        user.setAddress(addressService.isExisting(user.getAddress(), changedBy));

        User created = (User) userCrud.add(user);

        changeLogService.logChangeSafely(new Change(
                ENTITY_TYPE, created.getId(), "CREATE",
                null, null, created.toString(), changedBy));

        return created;
    }

    /**
     * @param id the user id
     * @return the user
     * @throws NoSuchElementException if there is no user with this id
     */
    public User fetchUser(int id) {
        User user = (User) userCrud.getById(id);
        if (user == null) {
            throw new NoSuchElementException("No user found with id: " + id);
        }
        return user;
    }

    /**
     * @param username the login name
     * @return the user
     * @throws NoSuchElementException if there is no user with this username
     */
    public User fetchByUsername(String username) {
        User user = userCrud.fetchByUsername(username);
        if (user == null) {
            throw new NoSuchElementException("No user found with username: " + username);
        }
        return user;
    }

    /**
     * Saves the new values and records one change per field that differs.
     *
     * @param incomingUser the user with the new values (and the existing id)
     * @param changedBy    username of the person making the change
     * @return the updated user
     */
    public User updateUser(User incomingUser, String changedBy) {
        validate(incomingUser);

        User existing = fetchUser(incomingUser.getId());
        int id = incomingUser.getId();

        Address resolvedAddress = addressService.resolveAddress(
                existing.getAddress(), incomingUser.getAddress(), changedBy);
        incomingUser.setAddress(resolvedAddress);

        List<Change> changes = new ArrayList<>();
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "first name",
                existing.getFirstName(), incomingUser.getFirstName(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "last name",
                existing.getLastName(), incomingUser.getLastName(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "username",
                existing.getUsername(), incomingUser.getUsername(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "oib",
                existing.getOib(), incomingUser.getOib(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "date of birth",
                existing.getDateOfBirth(), incomingUser.getDateOfBirth(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "email",
                existing.getEmail(), incomingUser.getEmail(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "address",
                existing.getAddress(), resolvedAddress, changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "role",
                existing.getRole(), incomingUser.getRole(), changedBy);

        userCrud.update(incomingUser);
        changes.forEach(changeLogService::logChangeSafely);

        return incomingUser;
    }

    /**
     * @param id        the user id
     * @param changedBy username of the person making the change
     * @return {@code true} if the user was deleted
     */
    public boolean deleteUser(int id, String changedBy) {
        User existing = fetchUser(id);

        boolean deleted = userCrud.delete(id);

        if (deleted) {
            changeLogService.logChangeSafely(new Change(
                    ENTITY_TYPE, id, "DELETE",
                    null, existing.toString(), null, changedBy));
        }

        return deleted;
    }
}
