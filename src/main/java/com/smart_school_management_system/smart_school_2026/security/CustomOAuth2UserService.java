package com.smart_school_management_system.smart_school_2026.security;

import com.smart_school_management_system.smart_school_2026.entity.Role;
import com.smart_school_management_system.smart_school_2026.entity.User;
import com.smart_school_management_system.smart_school_2026.repository.UserRepository;
import com.smart_school_management_system.smart_school_2026.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final EmailService emailService; // ✅ Inject EmailService

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oAuth2User = super.loadUser(userRequest);
            Map<String, Object> attributes = oAuth2User.getAttributes();

            log.info("=== OAuth2 User Attributes ===");
            log.info("Attributes: {}", attributes);

            // Get provider name
            String provider = userRequest.getClientRegistration().getRegistrationId();
            log.info("Provider: {}", provider);

            String email = null;
            String name = null;
            String picture = null;

            // Google se user info extract
            if ("google".equals(provider)) {
                email = (String) attributes.get("email");
                name = (String) attributes.get("name");
                picture = (String) attributes.get("picture");
            }
            // GitHub se user info extract
            else if ("github".equals(provider)) {
                email = (String) attributes.get("email");
                if (email == null || email.isEmpty()) {
                    email = (String) attributes.get("login") + "@github.com";
                }
                name = (String) attributes.get("name");
                if (name == null || name.isEmpty()) {
                    name = (String) attributes.get("login");
                }
                picture = (String) attributes.get("avatar_url");
            }

            log.info("Email: {}", email);
            log.info("Name: {}", name);
            log.info("Provider: {}", provider);

            if (email == null || email.isEmpty()) {
                log.error("❌ Email not found in OAuth2 response");
                throw new OAuth2AuthenticationException("Email not found from " + provider);
            }

            log.info("✅ Processing OAuth2 user with email: {}", email);

            // Check if user exists in database
            Optional<User> existingUser = userRepository.findByEmail(email);
            User user;
            boolean isNewUser = false;

            if (existingUser.isPresent()) {
                user = existingUser.get();
                log.info("✅ User found in database: {}", user.getUsername());
            } else {
                // Create new user
                isNewUser = true;
                log.info("🆕 Creating new user from {} OAuth2", provider);
                user = new User();
                user.setUsername(email);
                user.setEmail(email);
                user.setFullName(name != null ? name : email);
                user.setProfilePic(picture);
                user.setRole(Role.STUDENT); // Default role
                user.setIsActive(true);
                user.setPassword(""); // OAuth users ke liye password nahi chahiye
                user = userRepository.save(user);
                log.info("✅ New user created via {}: {}", provider, email);
            }

            // ✅ Send email notification after successful login
            try {
                if (isNewUser) {
                    emailService.sendWelcomeEmail(email, user.getFullName(), provider);
                    log.info("📧 Welcome email sent to: {}", email);
                } else {
                    emailService.sendLoginNotificationEmail(email, user.getFullName(), provider);
                    log.info("📧 Login notification email sent to: {}", email);
                }
            } catch (Exception e) {
                log.error("❌ Failed to send email notification: {}", e.getMessage());
                // Email fail hone par bhi login continue karein
            }

            // Return OAuth2User with authorities
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );

            return new DefaultOAuth2User(authorities, attributes,
                    "google".equals(provider) ? "email" : "id");

        } catch (Exception e) {
            log.error("❌ Error in OAuth2 loadUser: {}", e.getMessage(), e);
            throw new OAuth2AuthenticationException("Error processing OAuth2 user: " + e.getMessage());
        }
    }
}