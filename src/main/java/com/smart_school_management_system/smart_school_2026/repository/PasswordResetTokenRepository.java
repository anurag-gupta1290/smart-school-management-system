package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByEmailAndOtp(String email, String otp);

    Optional<PasswordResetToken> findByEmail(String email);

    void deleteByEmail(String email);

    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.isUsed = true WHERE t.id = :id")
    void markAsUsed(@Param("id") Long id);
}