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
     * Welcome Email Body - Simple & Clean
     */
    private String getWelcomeEmailBody(String userName, String provider) {
        String providerDisplay = provider.equals("google") ? "Google" : "GitHub";

        return "🎉 Welcome to " + APP_NAME + "!\n\n" +
                "Hello " + userName + ",\n\n" +
                "Your account has been successfully created using " + providerDisplay + " login.\n\n" +
                "📧 Email: " + userName + "\n" +
                "🔐 Login Method: " + providerDisplay + "\n" +
                "🕐 Time: " + getCurrentTime() + "\n\n" +
                "You can now access your dashboard at:\n" +
                "🔗 http://localhost:8083/login.html\n\n" +
                "If you did not create this account, please ignore this email or contact support.\n\n" +
                "Thanks,\n" +
                APP_NAME + " Team";
    }

    /**
     * Login Notification Email Body - Simple & Clean
     */
    private String getLoginNotificationBody(String userName, String provider) {
        String providerDisplay = provider.equals("google") ? "Google" : "GitHub";

        return "🔐 Login Notification - " + APP_NAME + "\n\n" +
                "Hello " + userName + ",\n\n" +
                "You have successfully logged in to your account using " + providerDisplay + " login.\n\n" +
                "📧 Email: " + userName + "\n" +
                "🔐 Login Method: " + providerDisplay + "\n" +
                "🕐 Time: " + getCurrentTime() + "\n\n" +
                "If this was not you, please reset your password immediately and contact support.\n\n" +
                "🔗 http://localhost:8083/login.html\n\n" +
                "Thanks,\n" +
                APP_NAME + " Team";
    }

    /**
     * OTP Email Body - Simple & Clean
     */
    private String getOTPEmailBody(String userName, String otp) {
        return "🔑 Password Reset OTP - " + APP_NAME + "\n\n" +
                "Hello " + userName + ",\n\n" +
                "You have requested to reset your password.\n\n" +
                "Your OTP is: " + otp + "\n\n" +
                "This OTP is valid for 5 minutes only.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Thanks,\n" +
                APP_NAME + " Team";
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