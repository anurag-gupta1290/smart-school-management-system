package com.smart_school_management_system.smart_school_2026.controller;

import com.smart_school_management_system.smart_school_2026.dto.NoticeDTO;
import com.smart_school_management_system.smart_school_2026.dto.NoticeRequest;
import com.smart_school_management_system.smart_school_2026.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // ============================================================
    // ADMIN ENDPOINTS
    // ============================================================

    /**
     * Admin: Get all notices
     * GET /api/admin/notices
     */
    @GetMapping("/admin/notices")
    public ResponseEntity<?> getAllNotices() {
        try {
            List<NoticeDTO> notices = noticeService.getAllNotices();
            return ResponseEntity.ok(notices);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Admin: Create notice
     * POST /api/admin/notices
     */
    @PostMapping("/admin/notices")
    public ResponseEntity<?> createNotice(@RequestBody NoticeRequest request) {
        try {
            request.setCreatedByRole("ADMIN");
            NoticeDTO created = noticeService.createNotice(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notice created",
                    "data", created
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Admin: Update notice
     * PUT /api/admin/notices/{id}
     */
    @PutMapping("/admin/notices/{id}")
    public ResponseEntity<?> updateNotice(
            @PathVariable Long id,
            @RequestBody NoticeRequest request) {
        try {
            NoticeDTO updated = noticeService.updateNotice(id, request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notice updated",
                    "data", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Admin: Delete notice (soft)
     * DELETE /api/admin/notices/{id}
     */
    @DeleteMapping("/admin/notices/{id}")
    public ResponseEntity<?> deleteNotice(@PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notice deleted"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Admin: Hard delete
     * DELETE /api/admin/notices/{id}/hard
     */
    @DeleteMapping("/admin/notices/{id}/hard")
    public ResponseEntity<?> hardDeleteNotice(@PathVariable Long id) {
        try {
            noticeService.hardDeleteNotice(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notice permanently deleted"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // TEACHER ENDPOINTS
    // ============================================================

    /**
     * Teacher: Get notices (admin + own)
     * GET /api/teacher/notices/{teacherId}
     */
    @GetMapping("/teacher/notices/{teacherId}")
    public ResponseEntity<?> getTeacherNotices(@PathVariable Long teacherId) {
        try {
            List<NoticeDTO> notices = noticeService.getTeacherNotices(teacherId);
            return ResponseEntity.ok(notices);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Teacher: Create notice (sirf students ke liye)
     * POST /api/teacher/notices
     */
    @PostMapping("/teacher/notices")
    public ResponseEntity<?> createTeacherNotice(@RequestBody NoticeRequest request) {
        try {
            request.setCreatedByRole("TEACHER");

            // Teacher sirf STUDENTS ya CLASS_SPECIFIC notices bana sakta hai
            if (!"STUDENTS".equals(request.getTargetAudience())
                    && !"CLASS_SPECIFIC".equals(request.getTargetAudience())) {
                request.setTargetAudience("STUDENTS");
            }

            NoticeDTO created = noticeService.createNotice(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notice sent to students",
                    "data", created
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Teacher: Update own notice
     * PUT /api/teacher/notices/{id}
     */
    @PutMapping("/teacher/notices/{id}")
    public ResponseEntity<?> updateTeacherNotice(
            @PathVariable Long id,
            @RequestBody NoticeRequest request) {
        try {
            NoticeDTO updated = noticeService.updateNotice(id, request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notice updated",
                    "data", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Teacher: Delete own notice
     * DELETE /api/teacher/notices/{id}
     */
    @DeleteMapping("/teacher/notices/{id}")
    public ResponseEntity<?> deleteTeacherNotice(@PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notice deleted"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // NOTE: STUDENT ENDPOINTS MOVED TO StudentController
    // ============================================================
    // Student notices endpoints are now in StudentController:
    //   - GET  /api/student/notices/{classId}
    //   - POST /api/student/notices/{id}/view
    //   - GET  /api/student/notices/all
    // ============================================================

    // ============================================================
    // SHARED ENDPOINTS
    // ============================================================

    /**
     * Get single notice
     * GET /api/notices/{id}
     */
    @GetMapping("/notices/{id}")
    public ResponseEntity<?> getNoticeById(@PathVariable Long id) {
        try {
            NoticeDTO notice = noticeService.getNoticeById(id);
            return ResponseEntity.ok(notice);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Toggle pin
     * POST /api/notices/{id}/toggle-pin
     */
    @PostMapping("/notices/{id}/toggle-pin")
    public ResponseEntity<?> togglePin(@PathVariable Long id) {
        try {
            NoticeDTO updated = noticeService.togglePin(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Pin toggled",
                    "data", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}