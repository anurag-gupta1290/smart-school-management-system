package com.smart_school_management_system.smart_school_2026.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_results", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"quiz_id", "student_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "marks_obtained")
    private Integer marksObtained = 0;

    @Column(name = "total_marks")
    private Integer totalMarks = 0;

    private BigDecimal percentage;

    @Column(length = 2)
    private String grade;

    @Column(name = "attempt_date")
    private LocalDateTime attemptDate;

    @PrePersist
    protected void onCreate() {
        attemptDate = LocalDateTime.now();
        if (totalMarks != null && totalMarks > 0 && marksObtained != null) {
            percentage = BigDecimal.valueOf((marksObtained * 100.0) / totalMarks);
        }
    }
}