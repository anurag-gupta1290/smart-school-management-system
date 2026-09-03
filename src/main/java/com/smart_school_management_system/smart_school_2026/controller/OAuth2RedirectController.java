package com.smart_school_management_system.smart_school_2026.controller;

import com.smart_school_management_system.smart_school_2026.entity.Role;
import com.smart_school_management_system.smart_school_2026.entity.User;
import com.smart_school_management_system.smart_school_2026.repository.UserRepository;
import com.smart_school_management_system.smart_school_2026.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuth2RedirectController {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @GetMapping("/oauth2/redirect")
    public RedirectView redirectAfterOAuth2() {
        try {
            log.info("=== OAuth2 Redirect Controller Called ===");

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                log.error("❌ User not authenticated after OAuth2");
                return new RedirectView("http://localhost:8083/login.html?error=not_authenticated");
            }

            log.info("✅ Authentication: {}", authentication);

            // Get principal
            Object principal = authentication.getPrincipal();
            String email = null;

            if (principal instanceof OAuth2User) {
                OAuth2User oAuth2User = (OAuth2User) principal;
                email = (String) oAuth2User.getAttributes().get("email");
                log.info("✅ OAuth2User email from principal: {}", email);
            } else if (principal instanceof String) {
                email = (String) principal;
                log.info("✅ Principal is string: {}", email);
            } else {
                log.error("❌ Unknown principal type: {}", principal.getClass());
                return new RedirectView("http://localhost:8083/login.html?error=unknown_principal");
            }

            if (email == null || email.isEmpty()) {
                log.error("❌ Email not found in principal");
                return new RedirectView("http://localhost:8083/login.html?error=email_not_found");
            }

            log.info("✅ Email extracted: {}", email);

            // Find user in database
            Optional<User> userOpt = userRepository.findByEmail(email);
            User user;

            if (userOpt.isPresent()) {
                user = userOpt.get();
                log.info("✅ User found in database: {}", user.getUsername());
            } else {
                // Create new user if not exists
                log.info("🆕 Creating new user for email: {}", email);
                user = new User();
                user.setUsername(email);
                user.setEmail(email);
                user.setFullName(email);
                user.setRole(Role.STUDENT); // ✅ NOW Role is properly imported
                user.setIsActive(true);
                user.setPassword("");
                user = userRepository.save(user);
                log.info("✅ New user created: {}", email);
            }

            log.info("✅ User: {} with role: {}", user.getUsername(), user.getRole());

            // Generate JWT token
            String jwt = tokenProvider.generateToken(authentication);
            log.info("✅ JWT Token generated successfully");

            // Redirect to frontend with token
            String redirectUrl = "http://localhost:8083/login.html?token=" + jwt + "&oauth2=true&email=" + email;
            log.info("✅ Redirecting to: {}", redirectUrl);

            return new RedirectView(redirectUrl);

        } catch (Exception e) {
            log.error("❌ Error in OAuth2 redirect: {}", e.getMessage(), e);
            return new RedirectView("http://localhost:8083/login.html?error=" + e.getMessage());
        }
    }
}