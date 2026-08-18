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

    public void validate(Address address) {
        if (!(address == null)) {

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

    public Address isExisting (Address incomingAddress, String changedBy) {

        validate(incomingAddress);

        Address resultAddress = addressCrud.isExisting(incomingAddress);
        if(!(resultAddress.isExistingFlag())) {
            resultAddress = createAddress(resultAddress, changedBy);
        }
        return resultAddress;
    }

    public Address createAddress(Address address, String changedBy) {
        validate(address);

        Address created = (Address) addressCrud.add(address);

        logTheChange(new Change(
                ENTITY_TYPE, created.getId(), "CREATE",
                null, null, created.toString(), changedBy));

        return created;
    }

    public Address fetchAddress(int id) {
        Address address = (Address) addressCrud.getById(id);
        if (address == null) {
            throw new NoSuchElementException("No address found with id: " + id);
        }
        return address;
    }

    public Address updateAddress(Address incomingAddress, String changedBy) {
        validate(incomingAddress);

        Address existingAddress = fetchAddress(incomingAddress.getId());

        // compare each value between the existingProject and the incomingProject - if there is a change >
        // save it as 1 change in a list of changes

        List<Change> changes = new ArrayList<>();;

        if(!(existingAddress.getStreet().equals(incomingAddress.getStreet()))) {
            changes.add(new Change(ENTITY_TYPE, incomingAddress.getId(), "UPDATE", "street", existingAddress.getStreet(), incomingAddress.getStreet(), changedBy));
        }
        if(!(existingAddress.getHouseNumber().equals(incomingAddress.getHouseNumber()))) {
            changes.add(new Change(ENTITY_TYPE, incomingAddress.getId(), "UPDATE", "house number", existingAddress.getHouseNumber(), incomingAddress.getHouseNumber(), changedBy));
        }
        if(!(existingAddress.getPostalCode().equals(incomingAddress.getPostalCode()))) {
            changes.add(new Change(ENTITY_TYPE, incomingAddress.getId(), "UPDATE", "postal code", existingAddress.getPostalCode(), incomingAddress.getPostalCode(), changedBy));
        }
        if(!(existingAddress.getCity().equals(incomingAddress.getCity()))) {
            changes.add(new Change(ENTITY_TYPE, incomingAddress.getId(), "UPDATE", "city", existingAddress.getCity(), incomingAddress.getCity(), changedBy));
        }
        if(!(existingAddress.getStreet().equals(incomingAddress.getCountry()))) {
            changes.add(new Change(ENTITY_TYPE, incomingAddress.getId(), "UPDATE", "country", existingAddress.getCountry(), incomingAddress.getCountry(), changedBy));
        }

        addressCrud.update(incomingAddress);

        for (Change change : changes) {
            logTheChange(change);
        }

        return incomingAddress;

    }

    public boolean deleteAddress(int id, String changedBy) {
        Address existing = fetchAddress(id);

        boolean deleted = addressCrud.delete(id);

        if (deleted) {
            logTheChange(new Change(
                    ENTITY_TYPE, id, "DELETE",
                    null, existing.toString(), null, changedBy));
        }

        return deleted;
    }
}
