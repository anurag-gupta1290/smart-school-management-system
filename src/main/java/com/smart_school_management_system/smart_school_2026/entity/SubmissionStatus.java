package com.smart_school_management_system.smart_school_2026.entity;

public enum SubmissionStatus {
    PENDING("Pending"),
    SUBMITTED("Submitted"),
    GRADED("Graded"),
    LATE("Late"),
    REJECTED("Rejected");

    private final String displayName;

    SubmissionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}