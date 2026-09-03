package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.Attendance;
import com.smart_school_management_system.smart_school_2026.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Student attendance
    List<Attendance> findByStudentId(Long studentId);
    List<Attendance> findByStudentIdAndSubjectId(Long studentId, Long subjectId);

    // Class attendance
    List<Attendance> findByClassEntityId(Long classId);
    List<Attendance> findByClassEntityIdAndAttendanceDate(Long classId, LocalDate date);

    // Subject wise attendance
    List<Attendance> findBySubjectId(Long subjectId);
    List<Attendance> findBySubjectIdAndAttendanceDate(Long subjectId, LocalDate date);

    // Student + Subject + Date
    Optional<Attendance> findByStudentIdAndSubjectIdAndAttendanceDate(Long studentId, Long subjectId, LocalDate date);

    // Teacher marked attendance
    List<Attendance> findByMarkedById(Long teacherId);

    // Count present by student and subject
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.subject.id = :subjectId AND a.status = 'PRESENT'")
    Long countPresentByStudentAndSubject(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);

    // Count total by student and subject
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.subject.id = :subjectId")
    Long countTotalByStudentAndSubject(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);

    // Get attendance percentage for a student in a subject
    @Query("SELECT (COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) * 100.0 / COUNT(a)) " +
            "FROM Attendance a WHERE a.student.id = :studentId AND a.subject.id = :subjectId")
    Double getAttendancePercentageByStudentAndSubject(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);

    // Get today's attendance for a class and subject
    @Query("SELECT a FROM Attendance a WHERE a.classEntity.id = :classId AND a.subject.id = :subjectId AND a.attendanceDate = :date")
    List<Attendance> findTodayAttendance(@Param("classId") Long classId, @Param("subjectId") Long subjectId, @Param("date") LocalDate date);

    // Get attendance by student, subject and date range
    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.subject.id = :subjectId " +
            "AND a.attendanceDate BETWEEN :startDate AND :endDate")
    List<Attendance> findByStudentAndSubjectAndDateRange(@Param("studentId") Long studentId,
                                                         @Param("subjectId") Long subjectId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);
}