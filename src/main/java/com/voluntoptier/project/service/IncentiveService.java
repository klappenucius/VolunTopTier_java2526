package com.voluntoptier.project.service;

import com.voluntoptier.project.entities.Address;
import com.voluntoptier.project.entities.Change;
import com.voluntoptier.project.entities.Incentive;
import com.voluntoptier.project.entities.Provider;
import com.voluntoptier.project.repository.IncentiveCrud;
import com.voluntoptier.project.repository.ProviderCrud;

public class IncentiveService {
    private static final String ENTITY_TYPE = "Incentive";

    private final IncentiveCrud incentiveCrud;
    private final ChangeLogService changeLogService;
    private final ProviderService providerService;

    public IncentiveService(IncentiveCrud incentiveCrud, ChangeLogService changeLogService, ProviderService providerService) {
        this.incentiveCrud = incentiveCrud;
        this.changeLogService = changeLogService;
        this.providerService = providerService;
    }

    private void validate(Incentive incentive) {
        if(incentive == null) {
            throw new IllegalArgumentException("Incentive cannot be null");
        }
        if (incentive.getName() == null || incentive.getName().isBlank()) {
            throw new IllegalArgumentException("Incentive name is required");
        }
        if (incentive.getDescription() == null || incentive.getDescription().isBlank()) {
            throw new IllegalArgumentException("Incentive description is required");
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

    public Incentive createIncentive(Incentive incentive, String changedBy) {
        validate(incentive);

        Incentive created = (Incentive) incentiveCrud.add(incentive);

        logTheChange(new Change(
                ENTITY_TYPE, created.getId(), "CREATE",
                null, null, created.toString(), changedBy));

        return created;
    }
}
