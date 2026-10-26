package com.voluntoptier.project.service;

import com.voluntoptier.project.entities.Address;
import com.voluntoptier.project.entities.Change;
import com.voluntoptier.project.repository.AddressCrud;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Business rules for addresses. Identical addresses are stored only once and shared.
 */
@Service
public class AddressService {

    private static final String ENTITY_TYPE = "Address";

    private final AddressCrud addressCrud;
    private final ChangeLogService changeLogService;

    /**
     * @param addressCrud      data access for addresses
     * @param changeLogService records every change
     */
    public AddressService(AddressCrud addressCrud, ChangeLogService changeLogService) {
        this.addressCrud = addressCrud;
        this.changeLogService = changeLogService;
    }

    /**
     * @param address the address to check; {@code null} is allowed (no address)
     * @throws IllegalArgumentException if a field is missing
     */
    public void validate(Address address) {
        if (address == null) {
            return;
        }
        requireText(address.getStreet(), "Street");
        requireText(address.getHouseNumber(), "House number");
        requireText(address.getPostalCode(), "Postal code");
        requireText(address.getCity(), "City");
        requireText(address.getCountry(), "Country");
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    /**
     * Returns the saved address with the same fields, creating it first if it doesn't exist yet.
     *
     * @param incomingAddress the address entered by the user, or {@code null}
     * @param changedBy       username of the person making the change
     * @return the saved address (with id), or {@code null} if {@code incomingAddress} was null
     */
    public Address isExisting(Address incomingAddress, String changedBy) {
        if (incomingAddress == null) {
            return null;
        }
        validate(incomingAddress);

        Address resultAddress = addressCrud.isExisting(incomingAddress);
        if (!resultAddress.isExistingFlag()) {
            resultAddress = createAddress(resultAddress, changedBy);
        }
        return resultAddress;
    }

    /**
     * Decides which address an updated user or project should point to.
     *
     * @param existingAddress the address currently saved
     * @param incomingAddress the address from the update
     * @param changedBy       username of the person making the change
     * @return the existing address if nothing changed (keeps its id), otherwise the saved incoming one
     */
    public Address resolveAddress(Address existingAddress, Address incomingAddress, String changedBy) {
        if (Objects.equals(existingAddress, incomingAddress)) {
            return existingAddress;
        }
        return isExisting(incomingAddress, changedBy);
    }

    /**
     * @param address   the new address
     * @param changedBy username of the person making the change
     * @return the saved address, with its new id
     */
    public Address createAddress(Address address, String changedBy) {
        validate(address);

        Address created = (Address) addressCrud.add(address);

        changeLogService.logChangeSafely(new Change(
                ENTITY_TYPE, created.getId(), "CREATE",
                null, null, created.toString(), changedBy));

        return created;
    }

    /**
     * @param id the address id
     * @return the address
     * @throws NoSuchElementException if there is no address with this id
     */
    public Address fetchAddress(int id) {
        Address address = (Address) addressCrud.getById(id);
        if (address == null) {
            throw new NoSuchElementException("No address found with id: " + id);
        }
        return address;
    }

    /**
     * @param incomingAddress the address with its new values (and its existing id)
     * @param changedBy       username of the person making the change
     * @return the updated address
     */
    public Address updateAddress(Address incomingAddress, String changedBy) {
        validate(incomingAddress);

        Address existing = fetchAddress(incomingAddress.getId());
        int id = incomingAddress.getId();

        List<Change> changes = new ArrayList<>();
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "street",
                existing.getStreet(), incomingAddress.getStreet(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "house number",
                existing.getHouseNumber(), incomingAddress.getHouseNumber(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "postal code",
                existing.getPostalCode(), incomingAddress.getPostalCode(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "city",
                existing.getCity(), incomingAddress.getCity(), changedBy);
        ChangeLogService.addIfChanged(changes, ENTITY_TYPE, id, "country",
                existing.getCountry(), incomingAddress.getCountry(), changedBy);

        addressCrud.update(incomingAddress);
        changes.forEach(changeLogService::logChangeSafely);

        return incomingAddress;
    }

    /**
     * @param id        the address id
     * @param changedBy username of the person making the change
     * @return {@code true} if the address was deleted
     */
    public boolean deleteAddress(int id, String changedBy) {
        Address existing = fetchAddress(id);

        boolean deleted = addressCrud.delete(id);

        if (deleted) {
            changeLogService.logChangeSafely(new Change(
                    ENTITY_TYPE, id, "DELETE",
                    null, existing.toString(), null, changedBy));
        }

        return deleted;
    }
}
