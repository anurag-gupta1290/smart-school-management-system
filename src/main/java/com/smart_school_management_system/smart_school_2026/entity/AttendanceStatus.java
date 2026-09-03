package com.smart_school_management_system.smart_school_2026.entity;

public enum AttendanceStatus {
    PRESENT("Present"),
    ABSENT("Absent"),
    LATE("Late"),
    EXCUSED("Excused");

    private final String displayName;

    AttendanceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}