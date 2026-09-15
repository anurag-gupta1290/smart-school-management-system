package com.smart_school_management_system.smart_school_2026.service;

import com.smart_school_management_system.smart_school_2026.dto.NoticeDTO;
import com.smart_school_management_system.smart_school_2026.dto.NoticeRequest;
import com.smart_school_management_system.smart_school_2026.entity.Notice;
import com.smart_school_management_system.smart_school_2026.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    // ============================================================
    // CREATE
    // ============================================================
    public NoticeDTO createNotice(NoticeRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Title is required");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new RuntimeException("Content is required");
        }
        if (request.getCreatedBy() == null) {
            throw new RuntimeException("Creator ID is required");
        }
        if (request.getCreatedByRole() == null) {
            throw new RuntimeException("Creator role is required");
        }

        Notice notice = new Notice();
        notice.setTitle(request.getTitle().trim());
        notice.setContent(request.getContent().trim());
        notice.setCreatedBy(request.getCreatedBy());
        notice.setCreatedByRole(request.getCreatedByRole());
        notice.setTargetAudience(
                request.getTargetAudience() != null
                        ? request.getTargetAudience() : "ALL");
        notice.setTargetClassId(request.getTargetClassId());
        notice.setSubjectId(request.getSubjectId());
        notice.setAttachmentUrl(request.getAttachmentUrl());
        notice.setPriority(
                request.getPriority() != null
                        ? request.getPriority() : "NORMAL");
        notice.setExpiryDate(request.getExpiryDate());
        notice.setIsPinned(request.getIsPinned() != null
                ? request.getIsPinned() : false);
        notice.setIsActive(true);
        notice.setViewCount(0);
        notice.setPublishDate(LocalDateTime.now());

        Notice saved = noticeRepository.save(notice);
        return convertToDTO(saved);
    }

    // ============================================================
    // READ
    // ============================================================
    public List<NoticeDTO> getAllNotices() {
        return noticeRepository.findAllByOrderByIsPinnedDescPublishDateDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NoticeDTO> getTeacherNotices(Long teacherUserId) {
        // Teacher ko ye dikhega:
        // 1. Admin ke notices (target = ALL or TEACHERS)
        // 2. Apne khud ke notices
        List<Notice> adminNotices = noticeRepository.findTeacherNotices();
        List<Notice> ownNotices = noticeRepository
                .findByCreatedByOrderByIsPinnedDescPublishDateDesc(teacherUserId);

        // Merge and dedupe
        return java.util.stream.Stream.concat(
                        adminNotices.stream(),
                        ownNotices.stream())
                .distinct()
                .sorted((a, b) -> {
                    if (!a.getIsPinned().equals(b.getIsPinned())) {
                        return b.getIsPinned().compareTo(a.getIsPinned());
                    }
                    return b.getPublishDate().compareTo(a.getPublishDate());
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NoticeDTO> getStudentNotices(Long classId) {
        return noticeRepository.findStudentNotices(classId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public NoticeDTO getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found: " + id));
        return convertToDTO(notice);
    }

    // ============================================================
    // UPDATE
    // ============================================================
    public NoticeDTO updateNotice(Long id, NoticeRequest request) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found: " + id));

        if (request.getTitle() != null) notice.setTitle(request.getTitle());
        if (request.getContent() != null) notice.setContent(request.getContent());
        if (request.getTargetAudience() != null) notice.setTargetAudience(request.getTargetAudience());
        if (request.getTargetClassId() != null) notice.setTargetClassId(request.getTargetClassId());
        if (request.getSubjectId() != null) notice.setSubjectId(request.getSubjectId());
        if (request.getAttachmentUrl() != null) notice.setAttachmentUrl(request.getAttachmentUrl());
        if (request.getPriority() != null) notice.setPriority(request.getPriority());
        if (request.getExpiryDate() != null) notice.setExpiryDate(request.getExpiryDate());
        if (request.getIsPinned() != null) notice.setIsPinned(request.getIsPinned());

        Notice updated = noticeRepository.save(notice);
        return convertToDTO(updated);
    }

    // ============================================================
    // DELETE (soft)
    // ============================================================
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found: " + id));
        notice.setIsActive(false);
        noticeRepository.save(notice);
    }

    // Hard delete
    public void hardDeleteNotice(Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new RuntimeException("Notice not found: " + id);
        }
        noticeRepository.deleteById(id);
    }

    // ============================================================
    // TOGGLE PIN
    // ============================================================
    public NoticeDTO togglePin(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found: " + id));
        notice.setIsPinned(!notice.getIsPinned());
        Notice updated = noticeRepository.save(notice);
        return convertToDTO(updated);
    }

    // ============================================================
    // INCREMENT VIEW
    // ============================================================
    public void incrementViewCount(Long id) {
        Notice notice = noticeRepository.findById(id).orElse(null);
        if (notice != null) {
            notice.setViewCount(notice.getViewCount() + 1);
            noticeRepository.save(notice);
        }
    }

    // ============================================================
    // HELPER: Entity → DTO
    // ============================================================
    private NoticeDTO convertToDTO(Notice n) {
        NoticeDTO dto = new NoticeDTO();
        dto.setId(n.getId());
        dto.setTitle(n.getTitle());
        dto.setContent(n.getContent());
        dto.setCreatedBy(n.getCreatedBy());
        dto.setCreatedByRole(n.getCreatedByRole());
        dto.setTargetAudience(n.getTargetAudience());
        dto.setTargetClassId(n.getTargetClassId());
        dto.setSubjectId(n.getSubjectId());
        dto.setAttachmentUrl(n.getAttachmentUrl());
        dto.setPriority(n.getPriority());
        dto.setPublishDate(n.getPublishDate());
        dto.setExpiryDate(n.getExpiryDate());
        dto.setIsActive(n.getIsActive());
        dto.setIsPinned(n.getIsPinned());
        dto.setViewCount(n.getViewCount());
        dto.setCreatedAt(n.getCreatedAt());

        // Placeholders (baad me join queries)
        dto.setCreatedByName("User #" + n.getCreatedBy());
        if (n.getTargetClassId() != null) {
            dto.setTargetClassName("Class #" + n.getTargetClassId());
        }
        if (n.getSubjectId() != null) {
            dto.setSubjectName("Subject #" + n.getSubjectId());
        }

        return dto;
    }
}