package com.smart_school_management_system.smart_school_2026.entity;

public enum NotificationType {
    INFO("Information"),
    WARNING("Warning"),
    SUCCESS("Success"),
    ERROR("Error"),
    ALERT("Alert"),
    REMINDER("Reminder");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}