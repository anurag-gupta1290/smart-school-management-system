package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassEntityRepository extends JpaRepository<ClassEntity, Long> {

    Optional<ClassEntity> findByClassNameAndSection(String className, String section);

    List<ClassEntity> findByClassName(String className);

    List<ClassEntity> findByClassTeacherId(Long teacherId);

    @Query("SELECT c FROM ClassEntity c WHERE c.academicYear = :year")
    List<ClassEntity> findByAcademicYear(@Param("year") String year);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.classEntity.id = :classId")
    Long countStudentsByClassId(@Param("classId") Long classId);

    @Query("SELECT COUNT(cs) FROM ClassSubject cs WHERE cs.classEntity.id = :classId")
    Long countSubjectsByClassId(@Param("classId") Long classId);

    // ✅ ADD THIS METHOD - Count classes by teacher
    @Query("SELECT COUNT(c) FROM ClassEntity c WHERE c.classTeacher.id = :teacherId")
    Long countByClassTeacherId(@Param("teacherId") Long teacherId);


}