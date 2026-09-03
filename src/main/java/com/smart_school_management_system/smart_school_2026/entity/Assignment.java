package com.smart_school_management_system.smart_school_2026.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "max_marks")
    private Integer maxMarks = 100;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Submission> submissions;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}