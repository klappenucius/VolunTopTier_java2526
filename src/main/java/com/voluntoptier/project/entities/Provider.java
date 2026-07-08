package com.voluntoptier.project.entities;

public class Provider extends DBitem{
    protected String name;
    protected Address address;
    protected String contact;
    protected String oib;

    public Provider(String id, String name, String contact, String oib) {
        super(id);
        this.name = name;
        this.contact = contact;
        this.oib = oib;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getOib() {
        return oib;
    }

    public void setOib(String oib) {
        this.oib = oib;
    }

    public String toString() {
        String output = name +
                ", OIB: " + oib +
                ", contact: " + contact;
        return output;
    }

    public void print(){
        System.out.println(this.toString());
    }
}
