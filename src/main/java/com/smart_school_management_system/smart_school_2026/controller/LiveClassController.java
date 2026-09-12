package com.smart_school_management_system.smart_school_2026.controller;

import com.smart_school_management_system.smart_school_2026.dto.LiveClassDTO;
import com.smart_school_management_system.smart_school_2026.dto.LiveClassRequest;
import com.smart_school_management_system.smart_school_2026.service.LiveClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = "*")
public class LiveClassController {

    @Autowired
    private LiveClassService liveClassService;

    // ============================================================
    // TEACHER ENDPOINTS
    // ============================================================

    /**
     * Create a new live class
     * POST /api/teacher/live-classes
     */
    @PostMapping("/teacher/live-classes")
    public ResponseEntity<?> createLiveClass(@RequestBody LiveClassRequest request) {
        try {
            LiveClassDTO created = liveClassService.createLiveClass(request);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all live classes for a teacher
     * GET /api/teacher/live-classes/teacher/{teacherId}
     */
    @GetMapping("/teacher/live-classes/teacher/{teacherId}")
    public ResponseEntity<?> getTeacherLiveClasses(@PathVariable Long teacherId) {
        try {
            List<LiveClassDTO> classes = liveClassService.getTeacherLiveClasses(teacherId);
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get single live class details
     * GET /api/teacher/live-classes/{id}
     */
    @GetMapping("/teacher/live-classes/{id}")
    public ResponseEntity<?> getLiveClassById(@PathVariable Long id) {
        try {
            LiveClassDTO liveClass = liveClassService.getLiveClassById(id);
            return ResponseEntity.ok(liveClass);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update live class
     * PUT /api/teacher/live-classes/{id}
     */
    @PutMapping("/teacher/live-classes/{id}")
    public ResponseEntity<?> updateLiveClass(
            @PathVariable Long id,
            @RequestBody LiveClassRequest request) {
        try {
            LiveClassDTO updated = liveClassService.updateLiveClass(id, request);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Start a live class
     * POST /api/teacher/live-classes/{id}/start
     */
    @PostMapping("/teacher/live-classes/{id}/start")
    public ResponseEntity<?> startLiveClass(@PathVariable Long id) {
        try {
            LiveClassDTO started = liveClassService.startLiveClass(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Class started",
                    "data", started
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * End a live class
     * POST /api/teacher/live-classes/{id}/end
     */
    @PostMapping("/teacher/live-classes/{id}/end")
    public ResponseEntity<?> endLiveClass(@PathVariable Long id) {
        try {
            LiveClassDTO ended = liveClassService.endLiveClass(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Class ended",
                    "data", ended
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Cancel a live class
     * POST /api/teacher/live-classes/{id}/cancel
     */
    @PostMapping("/teacher/live-classes/{id}/cancel")
    public ResponseEntity<?> cancelLiveClass(@PathVariable Long id) {
        try {
            LiveClassDTO cancelled = liveClassService.cancelLiveClass(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Class cancelled",
                    "data", cancelled
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Delete a live class
     * DELETE /api/teacher/live-classes/{id}
     */
    @DeleteMapping("/teacher/live-classes/{id}")
    public ResponseEntity<?> deleteLiveClass(@PathVariable Long id) {
        try {
            liveClassService.deleteLiveClass(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Class deleted"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // STUDENT ENDPOINTS
    // ============================================================

    /**
     * Get all live classes for a student (based on enrolled class)
     * GET /api/student/live-classes/{studentId}
     */
    @GetMapping("/student/live-classes/{studentId}")
    public ResponseEntity<?> getStudentLiveClasses(@PathVariable Long studentId) {
        try {
            List<LiveClassDTO> classes = liveClassService.getStudentLiveClasses(studentId);
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Mark student as joined the live class
     * POST /api/student/live-classes/{id}/join
     */
    @PostMapping("/student/live-classes/{id}/join")
    public ResponseEntity<?> markStudentJoined(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body) {
        try {
            Long studentId = body.get("studentId");
            liveClassService.markStudentJoined(id, studentId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Joined successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}