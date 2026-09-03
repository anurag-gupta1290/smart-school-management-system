package com.smart_school_management_system.smart_school_2026.controller;

import com.smart_school_management_system.smart_school_2026.entity.User;
import com.smart_school_management_system.smart_school_2026.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = authService.registerUser(user);
            return ResponseEntity.ok(Map.of(
                    "message", "User registered successfully",
                    "userId", registeredUser.getId(),
                    "username", registeredUser.getUsername(),
                    "email", registeredUser.getEmail(),
                    "role", registeredUser.getRole().name()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");

            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username is required"));
            }
            if (password == null || password.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
            }

            Map<String, Object> response = authService.loginUser(username, password);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            User currentUser = authService.getCurrentUser();
            return ResponseEntity.ok(Map.of(
                    "userId", currentUser.getId(),
                    "username", currentUser.getUsername(),
                    "email", currentUser.getEmail(),
                    "fullName", currentUser.getFullName(),
                    "role", currentUser.getRole().name(),
                    "isActive", currentUser.getIsActive()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        // JWT is stateless, so logout is handled client-side
        // This endpoint is just for convenience
        return ResponseEntity.ok(Map.of(
                "message", "Logged out successfully",
                "info", "Please remove the token from client-side storage"
        ));
    }
}