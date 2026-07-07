package com.voluntoptier.project.entities;

public enum Incentive {
    DISCOUNT_COUPON("10% Discount Coupon for local stores", 10),
    TICKETS("Free entry to a community event", 25),
    ALL_U_CAN_EAT("All-you-can-eat buffet voucher", 40);

    private final String description;
    private final int pointsRequired;

    Incentive(String description, int pointsRequired) {
        this.description = description;
        this.pointsRequired = pointsRequired;
    }

    public String getDescription() {
        return description;
    }

    public int getPointsRequired() {
        return pointsRequired;
    }

    @Override
    public String toString() {
        return name() + " (" + description + ", requires " + pointsRequired + " pts)";
    }
}
