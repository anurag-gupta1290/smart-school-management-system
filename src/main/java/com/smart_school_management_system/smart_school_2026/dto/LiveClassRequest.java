package com.smart_school_management_system.smart_school_2026.dto;

import java.time.LocalDateTime;

/**
 * Request DTO for creating/updating a LiveClass
 * Used to receive data from frontend
 */
public class LiveClassRequest {

    // ============================================================
    // FIELDS
    // ============================================================
    private String title;
    private String description;

    private Long classId;
    private Long subjectId;
    private Long teacherId;

    private String meetingUrl;
    private String meetingPlatform;    // GOOGLE_MEET, ZOOM, TEAMS, OTHER
    private String meetingId;
    private String meetingPassword;

    private LocalDateTime scheduledAt;
    private Integer durationMinutes;

    private String recordingUrl;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    public LiveClassRequest() {
    }

    public LiveClassRequest(String title, String description,
                            Long classId, Long subjectId, Long teacherId,
                            String meetingUrl, String meetingPlatform,
                            String meetingId, String meetingPassword,
                            LocalDateTime scheduledAt, Integer durationMinutes) {
        this.title = title;
        this.description = description;
        this.classId = classId;
        this.subjectId = subjectId;
        this.teacherId = teacherId;
        this.meetingUrl = meetingUrl;
        this.meetingPlatform = meetingPlatform;
        this.meetingId = meetingId;
        this.meetingPassword = meetingPassword;
        this.scheduledAt = scheduledAt;
        this.durationMinutes = durationMinutes;
    }

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getMeetingUrl() {
        return meetingUrl;
    }

    public void setMeetingUrl(String meetingUrl) {
        this.meetingUrl = meetingUrl;
    }

    public String getMeetingPlatform() {
        return meetingPlatform;
    }

    public void setMeetingPlatform(String meetingPlatform) {
        this.meetingPlatform = meetingPlatform;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getMeetingPassword() {
        return meetingPassword;
    }

    public void setMeetingPassword(String meetingPassword) {
        this.meetingPassword = meetingPassword;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getRecordingUrl() {
        return recordingUrl;
    }

    public void setRecordingUrl(String recordingUrl) {
        this.recordingUrl = recordingUrl;
    }

    // ============================================================
    // toString
    // ============================================================
    @Override
    public String toString() {
        return "LiveClassRequest{" +
                "title='" + title + '\'' +
                ", classId=" + classId +
                ", subjectId=" + subjectId +
                ", teacherId=" + teacherId +
                ", meetingPlatform='" + meetingPlatform + '\'' +
                ", scheduledAt=" + scheduledAt +
                ", durationMinutes=" + durationMinutes +
                '}';
    }
}