package com.smart_school_management_system.smart_school_2026.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "quiz_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = "option_a", columnDefinition = "TEXT")
    private String optionA;

    @Column(name = "option_b", columnDefinition = "TEXT")
    private String optionB;

    @Column(name = "option_c", columnDefinition = "TEXT")
    private String optionC;

    @Column(name = "option_d", columnDefinition = "TEXT")
    private String optionD;

    @Column(name = "correct_answer", length = 10)
    private String correctAnswer;

    private Integer marks = 1;
}