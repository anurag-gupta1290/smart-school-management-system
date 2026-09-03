package com.smart_school_management_system.smart_school_2026.entity;

public enum Role {
    ADMIN("Administrator"),
    TEACHER("Teacher"),
    STUDENT("Student");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}