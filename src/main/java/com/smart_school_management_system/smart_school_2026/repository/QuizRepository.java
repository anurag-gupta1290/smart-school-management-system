package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.Quiz;
import com.smart_school_management_system.smart_school_2026.entity.QuizType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByClassEntityId(Long classId);
    List<Quiz> findByTeacherId(Long teacherId);
    List<Quiz> findBySubjectId(Long subjectId);
    List<Quiz> findByQuizType(QuizType quizType);
    List<Quiz> findByQuizDateBefore(LocalDateTime date);
    List<Quiz> findByQuizDateAfter(LocalDateTime date);
    List<Quiz> findByQuizDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT q FROM Quiz q WHERE q.classEntity.className = :className")
    List<Quiz> findByClassName(@Param("className") String className);

    @Query("SELECT q FROM Quiz q WHERE q.teacher.id = :teacherId AND q.quizDate > :currentDate")
    List<Quiz> findUpcomingQuizzesByTeacher(@Param("teacherId") Long teacherId, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT q FROM Quiz q WHERE q.classEntity.id = :classId AND q.quizDate > :currentDate")
    List<Quiz> findUpcomingQuizzesByClass(@Param("classId") Long classId, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.teacher.id = :teacherId")
    Long countByTeacherId(@Param("teacherId") Long teacherId);
}