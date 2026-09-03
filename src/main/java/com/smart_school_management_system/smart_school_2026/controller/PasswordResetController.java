package com.smart_school_management_system.smart_school_2026.controller;

import com.smart_school_management_system.smart_school_2026.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            log.info("📧 Forgot password request for: {}", email);

            String result = passwordResetService.generateAndSendOTP(email);

            return ResponseEntity.ok(Map.of(
                    "message", result,
                    "email", email,
                    "expiresIn", "1 minute"
            ));
        } catch (Exception e) {
            log.error("❌ Forgot password error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String otp = request.get("otp");

            log.info("🔐 Verifying OTP for: {}", email);

            boolean isValid = passwordResetService.verifyOTP(email, otp);

            return ResponseEntity.ok(Map.of(
                    "valid", isValid,
                    "message", "OTP verified successfully"
            ));
        } catch (Exception e) {
            log.error("❌ OTP verification error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String otp = request.get("otp");
            String newPassword = request.get("newPassword");

            log.info("🔑 Reset password request for: {}", email);

            // Validate password
            if (newPassword == null || newPassword.length() < 6) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 6 characters"));
            }

            passwordResetService.resetPassword(email, otp, newPassword);

            log.info("✅ Password reset successful for: {}", email);

            return ResponseEntity.ok(Map.of(
                    "message", "Password reset successfully"
            ));
        } catch (Exception e) {
            log.error("❌ Reset password error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOTP(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            log.info("🔄 Resend OTP request for: {}", email);

            String result = passwordResetService.resendOTP(email);

            return ResponseEntity.ok(Map.of(
                    "message", result,
                    "email", email,
                    "expiresIn", "1 minute"
            ));
        } catch (Exception e) {
            log.error("❌ Resend OTP error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}