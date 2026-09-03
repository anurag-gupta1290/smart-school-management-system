package com.smart_school_management_system.smart_school_2026.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "student", "teacher", "admin", "notifications"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "student_id", unique = true, nullable = false, length = 20)
    private String studentId;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "classTeacher", "students", "classSubjects", "attendances", "assignments", "quizzes"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;



    @Column(name = "class", length = 10)
    private String class_;

    @Column(length = 5)
    private String section;

    @Column(name = "roll_number")
    private Integer rollNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "guardian_name", length = 100)
    private String guardianName;

    @Column(name = "guardian_phone", length = 15)
    private String guardianPhone;

    @Column(name = "admission_date")
    private LocalDate admissionDate;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "student"})
    @OneToMany(
            mappedBy = "student",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<Attendance> attendances;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "student"})
    @OneToMany(
            mappedBy = "student",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<Submission> submissions;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "student"})
    @OneToMany(
            mappedBy = "student",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<QuizResult> quizResults;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "student"})
    @OneToMany(
            mappedBy = "student",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<Fee> fees;
}