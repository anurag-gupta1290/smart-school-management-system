package com.smart_school_management_system.smart_school_2026.service;

import com.smart_school_management_system.smart_school_2026.entity.*;
import com.smart_school_management_system.smart_school_2026.repository.*;
import com.smart_school_management_system.smart_school_2026.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ClassEntityRepository classEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public User registerUser(User user) {
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // ✅ FIX: Role ko FORCE karo (Sirf STUDENT hi register ho sakta hai)
        user.setRole(Role.STUDENT);

        user.setIsActive(true);

        // ✅ Save User
        User savedUser = userRepository.save(user);

        // ✅ Agar role STUDENT hai, toh Student table mein entry banao
        if (savedUser.getRole() == Role.STUDENT) {
            Student student = new Student();
            student.setUser(savedUser);
            student.setStudentId("STU-" + String.format("%04d", savedUser.getId()));
            student.setClass_("10-A"); // Default class
            student.setSection("A"); // Default section
            student.setRollNumber(0);
            student.setGuardianName("");
            student.setGuardianPhone("");

            // ✅ CLASS ID SET KARO (Class 10 = ID 10)
            ClassEntity classEntity = classEntityRepository.findByClassNameAndSection("10", "A").orElse(null);
            if (classEntity != null) {
                student.setClassEntity(classEntity);
            }

            studentRepository.save(student);
        }

        return savedUser;
    }

    public Map<String, Object> loginUser(String username, String password, String requestedRole) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        // Get user details
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ FIX: ROLE CHECK (Sabse Important!)
        String actualRole = user.getRole().name();
        if (requestedRole != null && !requestedRole.equalsIgnoreCase(actualRole)) {
            throw new RuntimeException("Role mismatch! " + actualRole + " can only login as " + actualRole);
        }

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("tokenType", "Bearer");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("fullName", user.getFullName());
        response.put("role", actualRole);
        response.put("isActive", user.getIsActive());

        // ✅ Add role-specific info (Student/Teacher/Admin)
        if (user.getRole() == Role.STUDENT && user.getStudent() != null) {
            response.put("studentId", user.getStudent().getStudentId());
            response.put("class", user.getStudent().getClass_());
            response.put("section", user.getStudent().getSection());
        } else if (user.getRole() == Role.TEACHER && user.getTeacher() != null) {
            response.put("teacherId", user.getTeacher().getTeacherId());
            response.put("department", user.getTeacher().getDepartment());
        } else if (user.getRole() == Role.ADMIN && user.getAdmin() != null) {
            response.put("adminId", user.getAdmin().getAdminId());
        }

        return response;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}