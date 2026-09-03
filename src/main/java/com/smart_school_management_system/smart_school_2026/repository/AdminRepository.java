package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByAdminId(String adminId);
    Optional<Admin> findByUserId(Long userId);

    @Query("SELECT a FROM Admin a JOIN a.user u WHERE u.username = :username")
    Optional<Admin> findByUsername(@Param("username") String username);
}