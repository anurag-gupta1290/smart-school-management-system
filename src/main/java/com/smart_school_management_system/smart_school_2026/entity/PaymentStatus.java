package com.smart_school_management_system.smart_school_2026.entity;

public enum PaymentStatus {
    PAID("Paid"),
    PENDING("Pending"),
    OVERDUE("Overdue"),
    PARTIAL("Partially Paid"),
    CANCELLED("Cancelled");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}