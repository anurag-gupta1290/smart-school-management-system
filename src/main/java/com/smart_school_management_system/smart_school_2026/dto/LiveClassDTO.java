package com.smart_school_management_system.smart_school_2026.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for LiveClass
 * Used to send live class data from backend to frontend
 */
public class LiveClassDTO {

    // ============================================================
    // FIELDS
    // ============================================================
    private Long id;
    private String title;
    private String description;

    // Class info
    private Long classId;
    private String className;
    private String section;

    // Subject info
    private Long subjectId;
    private String subjectName;

    // Teacher info
    private Long teacherId;
    private String teacherName;

    // Meeting details
    private String meetingUrl;
    private String meetingPlatform;   // GOOGLE_MEET, ZOOM, TEAMS, OTHER
    private String meetingPassword;

    // Schedule
    private LocalDateTime scheduledAt;
    private Integer durationMinutes;

    // Status
    private String status;            // SCHEDULED, LIVE, ENDED, CANCELLED
    private String recordingUrl;

    // Timestamps
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime createdAt;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    public LiveClassDTO() {
    }

    public LiveClassDTO(Long id, String title, String description,
                        Long classId, String className, String section,
                        Long subjectId, String subjectName,
                        Long teacherId, String teacherName,
                        String meetingUrl, String meetingPlatform, String meetingPassword,
                        LocalDateTime scheduledAt, Integer durationMinutes,
                        String status, String recordingUrl,
                        LocalDateTime startedAt, LocalDateTime endedAt,
                        LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.classId = classId;
        this.className = className;
        this.section = section;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.meetingUrl = meetingUrl;
        this.meetingPlatform = meetingPlatform;
        this.meetingPassword = meetingPassword;
        this.scheduledAt = scheduledAt;
        this.durationMinutes = durationMinutes;
        this.status = status;
        this.recordingUrl = recordingUrl;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.createdAt = createdAt;
    }

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecordingUrl() {
        return recordingUrl;
    }

    public void setRecordingUrl(String recordingUrl) {
        this.recordingUrl = recordingUrl;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ============================================================
    // toString
    // ============================================================
    @Override
    public String toString() {
        return "LiveClassDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", classId=" + classId +
                ", subjectId=" + subjectId +
                ", teacherId=" + teacherId +
                ", meetingPlatform='" + meetingPlatform + '\'' +
                ", scheduledAt=" + scheduledAt +
                ", status='" + status + '\'' +
                '}';
    }
}