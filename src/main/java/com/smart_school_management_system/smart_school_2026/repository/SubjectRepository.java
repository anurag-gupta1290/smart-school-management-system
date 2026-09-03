package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findBySubjectCode(String subjectCode);
    List<Subject> findBySubjectNameContaining(String subjectName);
    List<Subject> findByCredits(Integer credits);

    @Query("SELECT s FROM Subject s JOIN s.classSubjects cs WHERE cs.classEntity.id = :classId")
    List<Subject> findSubjectsByClassId(@Param("classId") Long classId);

    @Query("SELECT s FROM Subject s JOIN s.classSubjects cs WHERE cs.teacher.id = :teacherId")
    List<Subject> findSubjectsByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT s FROM Subject s WHERE s.id NOT IN (SELECT cs.subject.id FROM ClassSubject cs WHERE cs.classEntity.id = :classId)")
    List<Subject> findSubjectsNotAssignedToClass(@Param("classId") Long classId);

    boolean existsBySubjectCode(String subjectCode);
}