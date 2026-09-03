package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.Role;
import com.smart_school_management_system.smart_school_2026.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find by username or email
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);

    // Find by role
    List<User> findByRole(Role role);
    List<User> findByRoleOrderByFullNameAsc(Role role);

    // Find by active status
    List<User> findByIsActive(Boolean isActive);
    List<User> findByRoleAndIsActive(Role role, Boolean isActive);

    // Check existence
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameOrEmail(String username, String email);

    // Search by name
    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> searchByName(@Param("name") String name);

    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%')) AND u.role = :role")
    List<User> searchByNameAndRole(@Param("name") String name, @Param("role") Role role);

    // Find active users by role
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    List<User> findActiveUsersByRole(@Param("role") Role role);

    // Count by role
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(@Param("role") Role role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
    Long countActiveByRole(@Param("role") Role role);

    // Update operations
    @Modifying
    @Query("UPDATE User u SET u.isActive = false WHERE u.id = :userId")
    void deactivateUser(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE User u SET u.isActive = true WHERE u.id = :userId")
    void activateUser(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :userId")
    void updatePassword(@Param("userId") Long userId, @Param("password") String password);

    @Modifying
    @Query("UPDATE User u SET u.profilePic = :profilePic WHERE u.id = :userId")
    void updateProfilePic(@Param("userId") Long userId, @Param("profilePic") String profilePic);

    // Get users by date range
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<User> findUsersByCreatedDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Get recently created users
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    List<User> findRecentUsers();
}