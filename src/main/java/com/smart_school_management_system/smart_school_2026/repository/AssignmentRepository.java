package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByClassEntityId(Long classId);
    List<Assignment> findByTeacherId(Long teacherId);
    List<Assignment> findBySubjectId(Long subjectId);
    List<Assignment> findByDueDateBefore(LocalDateTime date);
    List<Assignment> findByDueDateAfter(LocalDateTime date);
    List<Assignment> findByDueDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM Assignment a WHERE a.classEntity.className = :className")
    List<Assignment> findByClassName(@Param("className") String className);

    @Query("SELECT a FROM Assignment a WHERE a.classEntity.id = :classId AND a.dueDate > :currentDate")
    List<Assignment> findUpcomingAssignments(@Param("classId") Long classId, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT a FROM Assignment a WHERE a.teacher.id = :teacherId AND a.dueDate > :currentDate")
    List<Assignment> findUpcomingAssignmentsByTeacher(@Param("teacherId") Long teacherId, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.teacher.id = :teacherId")
    Long countByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.classEntity.id = :classId")
    Long countByClassId(@Param("classId") Long classId);




}