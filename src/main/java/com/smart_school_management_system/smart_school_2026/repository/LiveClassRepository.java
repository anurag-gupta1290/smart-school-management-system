package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.LiveClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveClassRepository extends JpaRepository<LiveClass, Long> {

    // Teacher ki saari live classes
    List<LiveClass> findByTeacherIdOrderByScheduledAtDesc(Long teacherId);

    // Class ki saari live classes
    List<LiveClass> findByClassIdOrderByScheduledAtDesc(Long classId);

    // Status ke hisaab se (LIVE, SCHEDULED, ENDED, CANCELLED)
    List<LiveClass> findByStatusOrderByScheduledAtDesc(String status);

    // Teacher + Status filter
    List<LiveClass> findByTeacherIdAndStatusOrderByScheduledAtDesc(Long teacherId, String status);

    // Class + Status filter (student ke liye)
    List<LiveClass> findByClassIdAndStatusOrderByScheduledAtDesc(Long classId, String status);
}