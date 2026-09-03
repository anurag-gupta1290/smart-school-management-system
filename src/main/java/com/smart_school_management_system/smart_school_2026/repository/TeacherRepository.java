package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByTeacherId(String teacherId);
    Optional<Teacher> findByUserId(Long userId);

    List<Teacher> findByDepartment(String department);
    List<Teacher> findByDepartmentOrderByExperienceYearsDesc(String department);

    @Query("SELECT t FROM Teacher t JOIN t.user u WHERE u.isActive = true")
    List<Teacher> findAllActiveTeachers();

    @Query("SELECT t FROM Teacher t WHERE t.experienceYears >= :years")
    List<Teacher> findByExperienceYearsGreaterThanEqual(@Param("years") Integer years);

    @Query("SELECT COUNT(t) FROM Teacher t WHERE t.department = :department")
    Long countByDepartment(@Param("department") String department);

    @Query("SELECT t FROM Teacher t JOIN t.classSubjects cs WHERE cs.classEntity.id = :classId")
    List<Teacher> findTeachersByClassId(@Param("classId") Long classId);
}