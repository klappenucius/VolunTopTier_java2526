package com.voluntoptier.project.entities;

public class Provider extends DBitem{
    protected String name;
    protected Address address;
    protected String contact;
    protected String oib;

    private Provider(ProviderBuilder builder) {
        super(builder.id);
        this.name = builder.name;
        this.contact = builder.contact;
        this.oib = builder.oib;
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

    public static class ProviderBuilder {
        private final int id;
        private final String name;
        private final String contact;
        private final String oib;

        private Address address;

        public ProviderBuilder(int id, String name, String contact, String oib) {
            this.id = id;
            this.name = name;
            this.contact = contact;
            this.oib = oib;

            this.address = null;
        }

        public ProviderBuilder address(Address address) {
            this.address = address;
            return this;
        }

        public Provider build()  {
            return new Provider(this);
        }
    }


}
