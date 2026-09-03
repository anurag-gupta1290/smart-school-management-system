package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.ClassSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassSubjectRepository extends JpaRepository<ClassSubject, Long> {

    List<ClassSubject> findByClassEntityId(Long classId);
    List<ClassSubject> findByTeacherId(Long teacherId);
    List<ClassSubject> findBySubjectId(Long subjectId);

    Optional<ClassSubject> findByClassEntityIdAndSubjectId(Long classId, Long subjectId);

    @Query("SELECT cs FROM ClassSubject cs WHERE cs.classEntity.id = :classId AND cs.teacher.id = :teacherId")
    List<ClassSubject> findByClassIdAndTeacherId(@Param("classId") Long classId, @Param("teacherId") Long teacherId);

    @Query("SELECT COUNT(cs) FROM ClassSubject cs WHERE cs.teacher.id = :teacherId")
    Long countByTeacherId(@Param("teacherId") Long teacherId);
}