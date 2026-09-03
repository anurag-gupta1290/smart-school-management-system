package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.Fee;
import com.smart_school_management_system.smart_school_2026.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {

    List<Fee> findByStudentId(Long studentId);
    List<Fee> findByPaymentStatus(PaymentStatus status);
    List<Fee> findByStudentIdAndPaymentStatus(Long studentId, PaymentStatus status);
    List<Fee> findByDueDate(LocalDate dueDate);
    List<Fee> findByDueDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT SUM(f.amount) FROM Fee f WHERE f.student.id = :studentId")
    BigDecimal getTotalFeeByStudent(@Param("studentId") Long studentId);

    @Query("SELECT SUM(f.amount) FROM Fee f WHERE f.student.id = :studentId AND f.paymentStatus = 'PAID'")
    BigDecimal getTotalPaidByStudent(@Param("studentId") Long studentId);

    @Query("SELECT SUM(f.amount) FROM Fee f WHERE f.student.id = :studentId AND f.paymentStatus IN ('PENDING', 'OVERDUE')")
    BigDecimal getTotalPendingByStudent(@Param("studentId") Long studentId);

    @Query("SELECT f FROM Fee f WHERE f.dueDate < :currentDate AND f.paymentStatus != 'PAID'")
    List<Fee> findOverdueFees(@Param("currentDate") LocalDate currentDate);
}