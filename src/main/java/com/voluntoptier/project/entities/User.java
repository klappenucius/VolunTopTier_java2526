package com.voluntoptier.project.entities;

import java.time.LocalDate;

public class User extends DBitem {
    protected String firstName;
    protected String lastName;
    protected String username;
    protected String oib;
    protected LocalDate dateOfBirth;
    protected Address address;
    protected String email;
    protected Role role;
    protected HoursWorked totalHoursWorked;

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

    public HoursWorked getTotalHoursWorked() {
        return totalHoursWorked;
    }

    public void setTotalHoursWorked(HoursWorked totalHoursWorked) {
        this.totalHoursWorked = totalHoursWorked;
    }

    public User(int id, String firstName, String lastName, String username, String oib, LocalDate dateOfBirth, Address address, String email, Role role) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.oib = oib;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.email = email;
        this.role = role;
        this.totalHoursWorked = new HoursWorked(0, 0);
    }

    public String toString() {
       String output = firstName +
               lastName +
               ", date of birth: " + dateOfBirth +
               ", email: " + email +
               ", total hours worked: " + totalHoursWorked;
       return output;
    }

    public void print() {
        System.out.println(this.toString());
    }

}
