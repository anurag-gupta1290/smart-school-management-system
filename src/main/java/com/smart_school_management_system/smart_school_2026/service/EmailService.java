package com.smart_school_management_system.smart_school_2026.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String APP_NAME = "Smart School Management System";

    /**
     * Send welcome email to new user
     */
    public void sendWelcomeEmail(String toEmail, String userName, String provider) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("🎉 Welcome to " + APP_NAME + "!");
            message.setText(getWelcomeEmailBody(userName, provider));

            mailSender.send(message);
            log.info("✅ Welcome email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("❌ Failed to send welcome email: {}", e.getMessage());
            throw new RuntimeException("Failed to send welcome email");
        }
    }

    /**
     * Send login notification email
     */
    public void sendLoginNotificationEmail(String toEmail, String userName, String provider) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("🔐 Login Notification - " + APP_NAME);
            message.setText(getLoginNotificationBody(userName, provider));

            mailSender.send(message);
            log.info("✅ Login notification email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("❌ Failed to send login notification: {}", e.getMessage());
            throw new RuntimeException("Failed to send login notification");
        }
    }

    /**
     * Send OTP email
     */
    public void sendOTPEmail(String toEmail, String userName, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("🔑 Password Reset OTP - " + APP_NAME);
            message.setText(getOTPEmailBody(userName, otp));

            mailSender.send(message);
            log.info("✅ OTP email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("❌ Failed to send OTP email: {}", e.getMessage());
            throw new RuntimeException("Failed to send OTP email");
        }
    }

    /**
     * Welcome Email Body
     */
    private String getWelcomeEmailBody(String userName, String provider) {
        String providerDisplay = provider.equals("google") ? "Google" : "GitHub";

        return String.format("""
            ╔══════════════════════════════════════════════════════════════╗
            ║                                                              ║
            ║   🎉 Welcome to %s! 🎉                                     ║
            ║                                                              ║
            ║   Hello %s,                                                 ║
            ║                                                              ║
            ║   Your account has been successfully created using          ║
            ║   %s login.                                                ║
            ║                                                              ║
            ║   📧 Email: %s                                              ║
            ║   🔐 Login Method: %s                                      ║
            ║   🕐 Time: %s                                              ║
            ║                                                              ║
            ║   You can now access your dashboard at:                     ║
            ║   🔗 http://localhost:8083/login.html                      ║
            ║                                                              ║
            ║   If you did not create this account, please ignore this    ║
            ║   email or contact support.                                 ║
            ║                                                              ║
            ║   Thanks,                                                    ║
            ║   %s Team                                                  ║
            ║                                                              ║
            ╚══════════════════════════════════════════════════════════════╝
            """, APP_NAME, userName, providerDisplay, userName, providerDisplay,
                getCurrentTime(), APP_NAME);
    }

    /**
     * Login Notification Email Body
     */
    private String getLoginNotificationBody(String userName, String provider) {
        String providerDisplay = provider.equals("google") ? "Google" : "GitHub";

        return String.format("""
            ╔══════════════════════════════════════════════════════════════╗
            ║                                                              ║
            ║   🔐 Login Notification - %s                    ║
            ║                                                              ║
            ║   Hello %s,                                                 ║
            ║                                                              ║
            ║   You have successfully logged in to your account using     ║
            ║   %s login.                                                ║
            ║                                                              ║
            ║   📧 Email: %s                                              ║
            ║   🔐 Login Method: %s                                      ║
            ║   🕐 Time: %s                                              ║
            ║   🌐 IP Address: Secure Connection                         ║
            ║                                                              ║
            ║   If this was not you, please reset your password           ║
            ║   immediately and contact support.                          ║
            ║                                                              ║
            ║   🔗 http://localhost:8083/login.html                      ║
            ║                                                              ║
            ║   Thanks,                                                    ║
            ║   %s Team                                                  ║
            ║                                                              ║
            ╚══════════════════════════════════════════════════════════════╝
            """, APP_NAME, userName, providerDisplay, userName, providerDisplay,
                getCurrentTime(), APP_NAME);
    }

    /**
     * OTP Email Body
     */
    private String getOTPEmailBody(String userName, String otp) {
        return String.format("""
            ╔══════════════════════════════════════════════════════════════╗
            ║                                                              ║
            ║   🔑 Password Reset OTP - %s                   ║
            ║                                                              ║
            ║   Hello %s,                                                 ║
            ║                                                              ║
            ║   You have requested to reset your password.                ║
            ║                                                              ║
            ║   Your OTP is:                                              ║
            ║   ╔═══════════════════════════════════════════════════════╗  ║
            ║   ║                                                       ║  ║
            ║   ║             🔐  %s  🔐                           ║  ║
            ║   ║                                                       ║  ║
            ║   ╚═══════════════════════════════════════════════════════╝  ║
            ║                                                              ║
            ║   This OTP is valid for 1 minute only.                      ║
            ║                                                              ║
            ║   If you did not request this, please ignore this email.    ║
            ║                                                              ║
            ║   Thanks,                                                    ║
            ║   %s Team                                                  ║
            ║                                                              ║
            ╚══════════════════════════════════════════════════════════════╝
            """, APP_NAME, userName, otp, APP_NAME);
    }

    /**
     * Get current time
     */
    private String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return now.format(formatter);
    }
}