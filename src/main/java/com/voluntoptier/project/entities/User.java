package com.voluntoptier.project.entities;

import java.time.LocalDate;

/**
 * A person using the app: a volunteer, moderator or admin.
 * Hours are not stored on the user; a user's totals are summed from their project assignments.
 */
public class User extends DBitem {
    protected String firstName;
    protected String lastName;
    protected String username;
    protected String oib;
    protected LocalDate dateOfBirth;
    protected Address address;
    protected String email;
    protected Role role;

    /**
     * Creates a user.
     *
     * @param id          database id (0 for a user not saved yet)
     * @param firstName   first name
     * @param lastName    last name
     * @param username    unique login name
     * @param oib         unique personal identification number
     * @param dateOfBirth date of birth
     * @param address     home address
     * @param email       e-mail address
     * @param role        role that decides what the user may do
     */
    public User(int id, String firstName, String lastName, String username, String oib, LocalDate dateOfBirth,
                Address address, String email, Role role) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.oib = oib;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.email = email;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOib() {
        return oib;
    }

    public void setOib(String oib) {
        this.oib = oib;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName +
                ", date of birth: " + dateOfBirth +
                ", email: " + email +
                ", role: " + role;
    }

    @Override
    public void print() {
        System.out.println(this);
    }
}