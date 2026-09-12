package com.smart_school_management_system.smart_school_2026.service;

import com.smart_school_management_system.smart_school_2026.dto.LiveClassDTO;
import com.smart_school_management_system.smart_school_2026.dto.LiveClassRequest;
import com.smart_school_management_system.smart_school_2026.entity.LiveClass;
import com.smart_school_management_system.smart_school_2026.repository.LiveClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LiveClassService {

    @Autowired
    private LiveClassRepository liveClassRepository;

    // ============================================================
    // CREATE
    // ============================================================
    public LiveClassDTO createLiveClass(LiveClassRequest request) {
        // Validation
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Title is required");
        }
        if (request.getMeetingUrl() == null || request.getMeetingUrl().trim().isEmpty()) {
            throw new RuntimeException("Meeting URL is required");
        }
        if (request.getScheduledAt() == null) {
            throw new RuntimeException("Scheduled time is required");
        }
        if (request.getClassId() == null) {
            throw new RuntimeException("Class ID is required");
        }
        if (request.getSubjectId() == null) {
            throw new RuntimeException("Subject ID is required");
        }
        if (request.getTeacherId() == null) {
            throw new RuntimeException("Teacher ID is required");
        }

        LiveClass liveClass = new LiveClass();
        liveClass.setTitle(request.getTitle().trim());
        liveClass.setDescription(request.getDescription());
        liveClass.setClassId(request.getClassId());
        liveClass.setSubjectId(request.getSubjectId());
        liveClass.setTeacherId(request.getTeacherId());
        liveClass.setMeetingUrl(request.getMeetingUrl().trim());
        liveClass.setMeetingPlatform(
                request.getMeetingPlatform() != null
                        ? request.getMeetingPlatform()
                        : "GOOGLE_MEET"
        );
        liveClass.setMeetingId(request.getMeetingId());
        liveClass.setMeetingPassword(request.getMeetingPassword());
        liveClass.setScheduledAt(request.getScheduledAt());
        liveClass.setDurationMinutes(
                request.getDurationMinutes() != null
                        ? request.getDurationMinutes()
                        : 60
        );
        liveClass.setStatus("SCHEDULED");

        LiveClass saved = liveClassRepository.save(liveClass);
        return convertToDTO(saved);
    }

    // ============================================================
    // READ
    // ============================================================
    public List<LiveClassDTO> getTeacherLiveClasses(Long teacherId) {
        List<LiveClass> list = liveClassRepository
                .findByTeacherIdOrderByScheduledAtDesc(teacherId);
        return list.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LiveClassDTO getLiveClassById(Long id) {
        LiveClass lc = liveClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Live class not found: " + id));
        return convertToDTO(lc);
    }

    public List<LiveClassDTO> getStudentLiveClasses(Long studentId) {
        // TODO: Student ki class_id lookup karke filter karo
        // Filhal saari classes return kar rahe hain
        List<LiveClass> list = liveClassRepository.findAll();
        return list.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ============================================================
    // UPDATE
    // ============================================================
    public LiveClassDTO updateLiveClass(Long id, LiveClassRequest request) {
        LiveClass lc = liveClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Live class not found: " + id));

        if (request.getTitle() != null) lc.setTitle(request.getTitle());
        if (request.getDescription() != null) lc.setDescription(request.getDescription());
        if (request.getClassId() != null) lc.setClassId(request.getClassId());
        if (request.getSubjectId() != null) lc.setSubjectId(request.getSubjectId());
        if (request.getMeetingUrl() != null) lc.setMeetingUrl(request.getMeetingUrl());
        if (request.getMeetingPlatform() != null) lc.setMeetingPlatform(request.getMeetingPlatform());
        if (request.getMeetingId() != null) lc.setMeetingId(request.getMeetingId());
        if (request.getMeetingPassword() != null) lc.setMeetingPassword(request.getMeetingPassword());
        if (request.getScheduledAt() != null) lc.setScheduledAt(request.getScheduledAt());
        if (request.getDurationMinutes() != null) lc.setDurationMinutes(request.getDurationMinutes());
        if (request.getRecordingUrl() != null) lc.setRecordingUrl(request.getRecordingUrl());

        LiveClass updated = liveClassRepository.save(lc);
        return convertToDTO(updated);
    }

    // ============================================================
    // STATUS CHANGES
    // ============================================================
    public LiveClassDTO startLiveClass(Long id) {
        LiveClass lc = liveClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Live class not found: " + id));

        if ("ENDED".equals(lc.getStatus()) || "CANCELLED".equals(lc.getStatus())) {
            throw new RuntimeException("Cannot start a class that is " + lc.getStatus());
        }

        lc.setStatus("LIVE");
        lc.setStartedAt(LocalDateTime.now());

        LiveClass saved = liveClassRepository.save(lc);
        return convertToDTO(saved);
    }

    public LiveClassDTO endLiveClass(Long id) {
        LiveClass lc = liveClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Live class not found: " + id));

        lc.setStatus("ENDED");
        lc.setEndedAt(LocalDateTime.now());

        LiveClass saved = liveClassRepository.save(lc);
        return convertToDTO(saved);
    }

    public LiveClassDTO cancelLiveClass(Long id) {
        LiveClass lc = liveClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Live class not found: " + id));

        lc.setStatus("CANCELLED");

        LiveClass saved = liveClassRepository.save(lc);
        return convertToDTO(saved);
    }

    public void deleteLiveClass(Long id) {
        if (!liveClassRepository.existsById(id)) {
            throw new RuntimeException("Live class not found: " + id);
        }
        liveClassRepository.deleteById(id);
    }

    public void markStudentJoined(Long liveClassId, Long studentId) {
        // TODO: live_class_attendance table me insert karo
        System.out.println("✅ Student " + studentId + " joined live class " + liveClassId);
    }

    // ============================================================
    // HELPER: Entity → DTO
    // ============================================================
    private LiveClassDTO convertToDTO(LiveClass lc) {
        LiveClassDTO dto = new LiveClassDTO();
        dto.setId(lc.getId());
        dto.setTitle(lc.getTitle());
        dto.setDescription(lc.getDescription());
        dto.setClassId(lc.getClassId());
        dto.setSubjectId(lc.getSubjectId());
        dto.setTeacherId(lc.getTeacherId());
        dto.setMeetingUrl(lc.getMeetingUrl());
        dto.setMeetingPlatform(lc.getMeetingPlatform());
        dto.setMeetingPassword(lc.getMeetingPassword());
        dto.setScheduledAt(lc.getScheduledAt());
        dto.setDurationMinutes(lc.getDurationMinutes());
        dto.setStatus(lc.getStatus());
        dto.setRecordingUrl(lc.getRecordingUrl());
        dto.setStartedAt(lc.getStartedAt());
        dto.setEndedAt(lc.getEndedAt());
        dto.setCreatedAt(lc.getCreatedAt());

        // Placeholder - baad me join queries lagayenge
        dto.setClassName("Class #" + lc.getClassId());
        dto.setSubjectName("Subject #" + lc.getSubjectId());
        dto.setTeacherName("Teacher #" + lc.getTeacherId());

        return dto;
    }
}