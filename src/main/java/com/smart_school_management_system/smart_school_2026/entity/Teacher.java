package com.smart_school_management_system.smart_school_2026.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "student", "teacher", "admin", "notifications"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "teacher_id", unique = true, nullable = false, length = 20)
    private String teacherId;

    @Column(length = 50)
    private String department;

    @Column(length = 100)
    private String qualification;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "teacher"})
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClassSubject> classSubjects;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "teacher"})
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Assignment> assignments;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "teacher"})
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Quiz> quizzes;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "classTeacher"})
    @OneToMany(mappedBy = "classTeacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClassEntity> classesAsTeacher;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "markedBy"})
    @OneToMany(mappedBy = "markedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> attendancesMarked;
}