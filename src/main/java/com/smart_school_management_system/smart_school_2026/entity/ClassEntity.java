package com.smart_school_management_system.smart_school_2026.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_name", nullable = false, length = 20)
    private String className;

    @Column(length = 5)
    private String section;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "user", "classSubjects", "assignments", "quizzes", "classesAsTeacher", "attendancesMarked"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_teacher_id")
    private Teacher classTeacher;

    @Column(name = "academic_year", length = 10)
    private String academicYear;

    @Column(name = "room_number", length = 10)
    private String roomNumber;

    private Integer capacity = 30;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "classEntity"})
    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClassSubject> classSubjects;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "classEntity", "user"})
    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Student> students;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "classEntity"})
    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> attendances;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "classEntity"})
    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Assignment> assignments;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "classEntity"})
    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Quiz> quizzes;
}