package com.smart_school_management_system.smart_school_2026.service;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public String generateOtp(String email) {
        int otp = 100000 + random.nextInt(900000);
        otpStore.put(email, String.valueOf(otp));
        return String.valueOf(otp);
    }

    public boolean verifyOtp(String email, String enteredOtp) {
        String storedOtp = otpStore.get(email);
        if (storedOtp != null && storedOtp.equals(enteredOtp)) {
            otpStore.remove(email);
            return true;
        }
        return false;
    }
}