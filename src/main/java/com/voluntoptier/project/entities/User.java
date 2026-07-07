package com.voluntoptier.project.entities;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Set;

/**
 * Abstract base class representing a system user.
 * <p>
 * A user has personal information, address data, and associated
 * {@link Worklog} records.
 * </p>
 */
public class User {
    private int id;
    private Role role;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateOfBirth;
    private Address address;
    private LinkedList<Worklog> worklogs;

    private LinkedList<Incentive> incentives = new LinkedList<>();

    public User(int id, Role role, String firstName, String lastName, String email, LocalDate dateOfBirth, Address address, LinkedList<Worklog> worklogs) {
        this.id = id;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.worklogs = worklogs;
    }

    public User() {
        this.worklogs = new LinkedList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public LinkedList<Worklog> getWorklogs() {
        return worklogs;
    }

    public void setWorklogs(LinkedList<Worklog> worklogs) {
        this.worklogs = worklogs;
    }

    public LinkedList<Incentive> getIncentives() {
        return incentives;
    }

    public void setIncentives(LinkedList<Incentive> incentives) {
        this.incentives = incentives;
    }

    public String getFullName() {
        return firstName + lastName;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                ", worklogs=" + worklogs + '\'' +
                ", incentives=" + incentives +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true; // same reference
        if (object == null || getClass() != object.getClass()) return false; // null or different class

        User user = (User) object;

        if (id != user.id) return false;
        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
        if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) return false;
        if (dateOfBirth != null ? !dateOfBirth.equals(user.dateOfBirth) : user.dateOfBirth != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        return result;
    }

}
