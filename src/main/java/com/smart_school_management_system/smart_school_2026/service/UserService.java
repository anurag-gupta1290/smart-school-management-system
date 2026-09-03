package com.smart_school_management_system.smart_school_2026.service;

import com.smart_school_management_system.smart_school_2026.entity.Role;
import com.smart_school_management_system.smart_school_2026.entity.User;
import com.smart_school_management_system.smart_school_2026.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create new user
     */
    @Transactional
    public User createUser(User user) {
        // Validate username and email
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);

        log.info("Creating new user: {}", user.getUsername());
        return userRepository.save(user);
    }

    /**
     * Register user with role
     */
    @Transactional
    public User registerUser(User user, Role role) {
        user.setRole(role);
        return createUser(user);
    }

    /**
     * Update user
     */
    @Transactional
    public User updateUser(Long userId, User updatedUser) {
        User existing = getUserById(userId);

        existing.setFullName(updatedUser.getFullName());
        existing.setPhone(updatedUser.getPhone());
        existing.setAddress(updatedUser.getAddress());
        existing.setProfilePic(updatedUser.getProfilePic());

        // Update password if provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        log.info("Updated user: {}", userId);
        return userRepository.save(existing);
    }

    /**
     * Deactivate user
     */
    @Transactional
    public void deactivateUser(Long userId) {
        User user = getUserById(userId);
        user.setIsActive(false);
        userRepository.save(user);
        log.info("Deactivated user: {}", userId);
    }

    /**
     * Activate user
     */
    @Transactional
    public void activateUser(Long userId) {
        User user = getUserById(userId);
        user.setIsActive(true);
        userRepository.save(user);
        log.info("Activated user: {}", userId);
    }

    /**
     * Change user password
     */
    @Transactional
    public void changePassword(Long userId, String newPassword) {
        User user = getUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Changed password for user: {}", userId);
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    /**
     * Get user by username
     */
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get users by role
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    /**
     * Get active users by role
     */
    @Transactional(readOnly = true)
    public List<User> getActiveUsersByRole(Role role) {
        return userRepository.findActiveUsersByRole(role);
    }

    /**
     * Search users by name
     */
    @Transactional(readOnly = true)
    public List<User> searchUsers(String query) {
        return userRepository.searchByName(query);
    }

    /**
     * Get user statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findByIsActive(true).size();
        long inactiveUsers = totalUsers - activeUsers;

        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("inactiveUsers", inactiveUsers);

        // Role-wise distribution
        Map<String, Long> roleDistribution = new HashMap<>();
        for (Role role : Role.values()) {
            Long count = userRepository.countByRole(role);
            roleDistribution.put(role.name(), count);
        }
        stats.put("roleDistribution", roleDistribution);

        // Active users by role
        Map<String, Long> activeRoleDistribution = new HashMap<>();
        for (Role role : Role.values()) {
            Long count = userRepository.countActiveByRole(role);
            activeRoleDistribution.put(role.name(), count);
        }
        stats.put("activeRoleDistribution", activeRoleDistribution);

        return stats;
    }

    /**
     * Check if username exists
     */
    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}