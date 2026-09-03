package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    List<QuizResult> findByStudentId(Long studentId);
    List<QuizResult> findByQuizId(Long quizId);
    Optional<QuizResult> findByQuizIdAndStudentId(Long quizId, Long studentId);

    @Query("SELECT AVG(qr.percentage) FROM QuizResult qr WHERE qr.quiz.id = :quizId")
    Double getAveragePercentageByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT AVG(qr.percentage) FROM QuizResult qr WHERE qr.student.id = :studentId")
    Double getAveragePercentageByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.quiz.id = :quizId ORDER BY qr.percentage DESC")
    List<QuizResult> findTopPerformers(@Param("quizId") Long quizId);
}