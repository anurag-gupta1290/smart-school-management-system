package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.NotePurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotePurchaseRepository extends JpaRepository<NotePurchase, Long> {

    Optional<NotePurchase> findByNoteIdAndStudentId(Long noteId, Long studentId);

    List<NotePurchase> findByStudentId(Long studentId);

    boolean existsByNoteIdAndStudentId(Long noteId, Long studentId);
}