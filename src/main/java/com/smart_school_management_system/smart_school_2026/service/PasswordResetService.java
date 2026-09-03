package com.smart_school_management_system.smart_school_2026.service;

import com.smart_school_management_system.smart_school_2026.entity.PasswordResetToken;
import com.smart_school_management_system.smart_school_2026.entity.User;
import com.smart_school_management_system.smart_school_2026.repository.PasswordResetTokenRepository;
import com.smart_school_management_system.smart_school_2026.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // ✅ Use EmailService

    @Value("${otp.expiration.minutes:1}")
    private int otpExpirationMinutes;

    @Transactional
    public String generateAndSendOTP(String email) {
        log.info("📧 Generating OTP for: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        tokenRepository.deleteByEmail(email);

        String otp = generateOTP();
        log.info("🔑 OTP generated: {} for {}", otp, email);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(otpExpirationMinutes);
        PasswordResetToken token = new PasswordResetToken(email, otp, now, expiresAt);
        tokenRepository.save(token);

        // ✅ Send OTP email using EmailService
        emailService.sendOTPEmail(email, user.getFullName(), otp);

        return "OTP sent successfully to " + email;
    }

    @Transactional
    public boolean verifyOTP(String email, String otp) {
        log.info("🔐 Verifying OTP for: {}", email);

        PasswordResetToken token = tokenRepository.findByEmailAndOtp(email, otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (token.getIsUsed()) {
            throw new RuntimeException("OTP already used");
        }

        if (token.isExpired()) {
            throw new RuntimeException("OTP has expired. Please request a new one.");
        }

        log.info("✅ OTP verified for: {}", email);
        return true;
    }

    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        log.info("🔑 Resetting password for: {}", email);

        PasswordResetToken token = tokenRepository.findByEmailAndOtp(email, otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (token.getIsUsed()) {
            throw new RuntimeException("OTP already used");
        }

        if (token.isExpired()) {
            throw new RuntimeException("OTP has expired. Please request a new one.");
        }

        token.setIsUsed(true);
        tokenRepository.save(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.deleteByEmail(email);

        log.info("✅ Password reset successful for: {}", email);
    }

    @Transactional
    public String resendOTP(String email) {
        log.info("🔄 Resending OTP for: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        tokenRepository.deleteByEmail(email);

        String otp = generateOTP();
        log.info("🔑 New OTP: {} for {}", otp, email);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(otpExpirationMinutes);
        PasswordResetToken token = new PasswordResetToken(email, otp, now, expiresAt);
        tokenRepository.save(token);

        // ✅ Send OTP email using EmailService
        emailService.sendOTPEmail(email, user.getFullName(), otp);

        return "New OTP sent successfully";
    }

    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}