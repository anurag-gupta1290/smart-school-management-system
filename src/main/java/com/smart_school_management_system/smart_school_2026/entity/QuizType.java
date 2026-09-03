package com.smart_school_management_system.smart_school_2026.entity;

public enum QuizType {
    QUIZ("Quiz"),
    TEST("Test"),
    EXAM("Examination"),
    PRACTICAL("Practical"),
    ASSIGNMENT("Assignment");

    private final String displayName;

    QuizType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}