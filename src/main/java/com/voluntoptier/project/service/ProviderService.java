package com.voluntoptier.project.service;

import com.voluntoptier.project.entities.Address;
import com.voluntoptier.project.entities.Change;
import com.voluntoptier.project.entities.Provider;
import com.voluntoptier.project.repository.ProviderCrud;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ProviderService {

    private static final String ENTITY_TYPE = "Provider";

    private final ProviderCrud providerCrud;
    private final ChangeLogService changeLogService;
    private final AddressService addressService;

    public ProviderService(ProviderCrud providerCrud, ChangeLogService changeLogService, AddressService addressService) {
        this.providerCrud = providerCrud;
        this.changeLogService = changeLogService;
        this.addressService = addressService;
    }

    private void validate(Provider provider) {
        if(provider == null) {
            throw new IllegalArgumentException("Provider cannot be null");
        }
        if (provider.getName() == null || provider.getName().isBlank()) {
            throw new IllegalArgumentException("Provider name is required");
        }
        if (provider.getContact() == null || provider.getContact().isBlank()) {
            throw new IllegalArgumentException("Provider contact is required");
        }
        if (provider.getOib() == null || provider.getOib().isBlank()) {
            throw new IllegalArgumentException("Provider OIB is required");
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

    public Provider createProvider(Provider provider, String changedBy) {
        validate(provider);

        Address resolvedAddress = addressService.isExisting(provider.getAddress(), changedBy);
        provider.setAddress(resolvedAddress);

        Provider created = (Provider) providerCrud.add(provider);

        logTheChange(new Change(
                ENTITY_TYPE, created.getId(), "CREATE",
                null, null, created.toString(), changedBy));

        return created;
    }

    public Provider fetchProvider(int id) {
        Provider provider = (Provider) providerCrud.getById(id);
        if(provider == null) {
            throw new NoSuchElementException("No provider found with id: " + id);
        }
        return provider;
    }

    public Provider updateProvider(Provider incomingProvider, String changedBy) {
        validate(incomingProvider);

        Provider existingProvider = fetchProvider(incomingProvider.getId());

        List<Change> changes = new ArrayList<>();

        if(!existingProvider.getName().equals(incomingProvider.getName())) {
            changes.add(new Change(ENTITY_TYPE, incomingProvider.getId(), "UPDATE", "name", existingProvider.getName(), incomingProvider.getName(), changedBy));
        }
        if(!existingProvider.getContact().equals(incomingProvider.getContact())) {
            changes.add(new Change(ENTITY_TYPE, incomingProvider.getId(), "UPDATE", "contact", existingProvider.getContact(), incomingProvider.getContact(), changedBy));
        }
        if(!existingProvider.getOib().equals(incomingProvider.getOib())) {
            changes.add(new Change(ENTITY_TYPE, incomingProvider.getId(), "UPDATE", "oib", existingProvider.getOib(), incomingProvider.getOib(), changedBy));
        }
        Address existingAddress = existingProvider.getAddress();
        Address incomingAddress = incomingProvider.getAddress();
        if(existingAddress == null || !existingAddress.equals(incomingAddress)) {
            Address newAddress = addressService.isExisting(incomingAddress, changedBy);
            incomingProvider.setAddress(newAddress);
            changes.add(new Change(ENTITY_TYPE, incomingProvider.getId(), "UPDATE", "address",
                    existingAddress == null ? null : existingAddress.toString(),
                    newAddress.toString(), changedBy));
        }

        providerCrud.update(incomingProvider);

        for (Change change : changes) {
            logTheChange(change);
        }

        return incomingProvider;
    }

    public boolean deleteProvider(int id, String changedBy) {
        Provider existing = fetchProvider(id);

        boolean deleted = providerCrud.delete(id);

        if (deleted) {
            logTheChange(new Change(
                    ENTITY_TYPE, id, "DELETE",
                    null, existing.toString(), null, changedBy));
        }

        return deleted;
    }
}
