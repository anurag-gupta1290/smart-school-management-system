package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // Admin ke saare notices
    List<Notice> findByCreatedByAndCreatedByRoleOrderByIsPinnedDescPublishDateDesc(
            Long createdBy, String createdByRole);

    // Teachers ke saare notices
    List<Notice> findByCreatedByOrderByIsPinnedDescPublishDateDesc(Long createdBy);

    // Active notices
    List<Notice> findByIsActiveTrueOrderByIsPinnedDescPublishDateDesc();

    // All notices for admin
    List<Notice> findAllByOrderByIsPinnedDescPublishDateDesc();

    // Custom query: Student ke liye notices
    @Query("SELECT n FROM Notice n WHERE n.isActive = true " +
            "AND (n.expiryDate IS NULL OR n.expiryDate > CURRENT_TIMESTAMP) " +
            "AND (n.targetAudience = 'ALL' " +
            "     OR n.targetAudience = 'STUDENTS' " +
            "     OR (n.targetAudience = 'CLASS_SPECIFIC' AND n.targetClassId = :classId)) " +
            "ORDER BY n.isPinned DESC, n.publishDate DESC")
    List<Notice> findStudentNotices(@Param("classId") Long classId);

    // Teacher ke liye notices
    @Query("SELECT n FROM Notice n WHERE n.isActive = true " +
            "AND (n.expiryDate IS NULL OR n.expiryDate > CURRENT_TIMESTAMP) " +
            "AND (n.targetAudience = 'ALL' OR n.targetAudience = 'TEACHERS') " +
            "ORDER BY n.isPinned DESC, n.publishDate DESC")
    List<Notice> findTeacherNotices();
}