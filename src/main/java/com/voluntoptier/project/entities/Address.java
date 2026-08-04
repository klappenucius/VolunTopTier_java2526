package com.voluntoptier.project.entities;

public class Address extends DBitem {

    protected String street;

    protected String houseNumber;

    protected String postalCode;

    protected String city;

    protected String country;

    public Address(int id, String street, String houseNumber, String postalCode, String city, String country) {
        super(id);
        this.street = street;
        this.houseNumber = houseNumber;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String toString () {
        String output = street +
                " " + houseNumber +
                ", " + postalCode +
                ", " + city +
                ", " + country;
        return output;
    }

    public void print() {
        System.out.println(this.toString());
    }
}
