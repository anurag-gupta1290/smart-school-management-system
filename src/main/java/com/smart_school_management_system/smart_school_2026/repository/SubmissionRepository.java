package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.Submission;
import com.smart_school_management_system.smart_school_2026.entity.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    // ============================================================
    // BASIC FIND METHODS
    // ============================================================

    List<Submission> findByStudentId(Long studentId);
    List<Submission> findByAssignmentId(Long assignmentId);
    List<Submission> findByStatus(SubmissionStatus status);
    List<Submission> findByStudentIdAndStatus(Long studentId, SubmissionStatus status);
    List<Submission> findByAssignmentIdAndStatus(Long assignmentId, SubmissionStatus status);

    // ============================================================
    // UNIQUE SUBMISSION CHECK
    // ============================================================

    Optional<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    // ============================================================
    // CUSTOM QUERIES
    // ============================================================

    @Query("SELECT COUNT(s) > 0 FROM Submission s WHERE s.assignment.id = :assignmentId AND s.student.id = :studentId AND s.status = 'SUBMITTED'")
    boolean hasSubmitted(@Param("assignmentId") Long assignmentId, @Param("studentId") Long studentId);

    @Query("SELECT AVG(s.marksObtained) FROM Submission s WHERE s.assignment.id = :assignmentId AND s.marksObtained IS NOT NULL")
    Double getAverageMarksByAssignmentId(@Param("assignmentId") Long assignmentId);

    @Query("SELECT s FROM Submission s WHERE s.marksObtained IS NULL AND s.status = 'SUBMITTED'")
    List<Submission> findPendingGrading();

    // ============================================================
    // DATE RANGE QUERIES
    // ============================================================

    List<Submission> findBySubmissionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // ============================================================
    // ✅ FIXED: Find by Student and Assignment (using assignment.subject instead of course)
    // ============================================================

    // ❌ REMOVE THIS - It's causing the error
    // @Query("SELECT s FROM Submission s WHERE s.student.id = :studentId AND s.assignment.course.id = :courseId")
    // List<Submission> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    // ✅ REPLACE WITH THIS - Using subject instead of course
    @Query("SELECT s FROM Submission s WHERE s.student.id = :studentId AND s.assignment.subject.id = :subjectId")
    List<Submission> findByStudentIdAndSubjectId(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);

    // ============================================================
    // OTHER USEFUL QUERIES
    // ============================================================

    @Query("SELECT s FROM Submission s WHERE s.submissionDate > s.assignment.dueDate AND s.status = 'SUBMITTED'")
    List<Submission> findLateSubmissions();

    @Query("SELECT s.status, COUNT(s) FROM Submission s WHERE s.assignment.id = :assignmentId GROUP BY s.status")
    List<Object[]> countSubmissionsByStatus(@Param("assignmentId") Long assignmentId);

    List<Submission> findByAssignmentIdAndMarksObtainedGreaterThan(Long assignmentId, Double marks);

    @Query("SELECT s FROM Submission s WHERE s.assignment.id = :assignmentId ORDER BY s.marksObtained DESC")
    List<Submission> findTopSubmissionsByAssignment(@Param("assignmentId") Long assignmentId, Pageable pageable);

    @Query("SELECT MIN(s.marksObtained), MAX(s.marksObtained) FROM Submission s WHERE s.assignment.id = :assignmentId")
    List<Object[]> getMinMaxMarksByAssignmentId(@Param("assignmentId") Long assignmentId);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.assignment.id = :assignmentId AND s.status = :status")
    long countByAssignmentIdAndStatus(@Param("assignmentId") Long assignmentId, @Param("status") SubmissionStatus status);

    // ============================================================
    // ADDITIONAL USEFUL QUERIES
    // ============================================================

    // Count submissions by student
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.student.id = :studentId")
    long countByStudentId(@Param("studentId") Long studentId);

    // Get submissions by student with assignment details
    @Query("SELECT s FROM Submission s JOIN FETCH s.assignment WHERE s.student.id = :studentId")
    List<Submission> findByStudentIdWithAssignment(@Param("studentId") Long studentId);

    // Get graded submissions by student
    List<Submission> findByStudentIdAndStatusOrderBySubmissionDateDesc(Long studentId, SubmissionStatus status);

    // Get pending submissions for grading
    @Query("SELECT s FROM Submission s WHERE s.marksObtained IS NULL AND s.status IN ('SUBMITTED', 'PENDING')")
    List<Submission> findPendingGradingSubmissions();

    // Get submissions by student and assignment statuses
    @Query("SELECT s FROM Submission s WHERE s.student.id = :studentId AND s.status IN :statuses")
    List<Submission> findByStudentIdAndStatuses(@Param("studentId") Long studentId, @Param("statuses") List<SubmissionStatus> statuses);

    // Get average marks for a student
    @Query("SELECT AVG(s.marksObtained) FROM Submission s WHERE s.student.id = :studentId AND s.marksObtained IS NOT NULL")
    Double getAverageMarksByStudentId(@Param("studentId") Long studentId);

    // Get submissions by assignment and student with grading status
    @Query("SELECT s FROM Submission s WHERE s.assignment.id = :assignmentId AND s.student.id = :studentId")
    Optional<Submission> findByAssignmentAndStudent(@Param("assignmentId") Long assignmentId, @Param("studentId") Long studentId);
}