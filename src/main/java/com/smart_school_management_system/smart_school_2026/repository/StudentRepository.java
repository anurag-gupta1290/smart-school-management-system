package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // ============================================================
    // FIND BY STUDENT ID
    // ============================================================
    Optional<Student> findByStudentId(String studentId);
    Optional<Student> findByUserId(Long userId);

    // ============================================================
    // FIND BY CLASS - Using @Query to avoid reserved keyword issue
    // ============================================================

    // Find by ClassEntity ID
    List<Student> findByClassEntityId(Long classId);
    List<Student> findByClassEntityIdOrderByRollNumberAsc(Long classId);

    // ✅ FIXED: Using @Query for class_ field
    @Query("SELECT s FROM Student s WHERE s.class_ = :className")
    List<Student> findByClassName(@Param("className") String className);

    // ✅ FIXED: Using @Query for class_ and section
    @Query("SELECT s FROM Student s WHERE s.class_ = :className AND s.section = :section")
    List<Student> findByClassAndSection(
            @Param("className") String className,
            @Param("section") String section
    );

    // ============================================================
    // COUNT METHODS - Using @Query to avoid reserved keyword issue
    // ============================================================

    // Count by class (using ClassEntity)
    @Query("SELECT COUNT(s) FROM Student s WHERE s.classEntity.id = :classId")
    long countByClassId(@Param("classId") Long classId);

    // ✅ FIXED: Count by class_ field
    @Query("SELECT COUNT(s) FROM Student s WHERE s.class_ = :className")
    long countByClass_(@Param("className") String className);

    // ✅ FIXED: Count by class_ and section
    @Query("SELECT COUNT(s) FROM Student s WHERE s.class_ = :className AND s.section = :section")
    long countByClassAndSection(
            @Param("className") String className,
            @Param("section") String section
    );

    // ❌ REMOVE THESE - They cause the error
    // long countByClassEntityId(Long classEntityId);
    // long countByClass_(String class_);
    // long countByClass_AndSection(String class_, String section);

    // ============================================================
    // OTHER FIND METHODS
    // ============================================================
    List<Student> findByGuardianNameContaining(String guardianName);
    List<Student> findBySection(String section);

    // ============================================================
    // SEARCH
    // ============================================================
    @Query("""
            SELECT s
            FROM Student s
            WHERE LOWER(s.user.fullName)
            LIKE LOWER(CONCAT('%', :name, '%'))
            """)
    List<Student> searchByName(@Param("name") String name);

    // ============================================================
    // FIND BY CLASS AND ROLL NUMBER
    // ============================================================
    @Query("""
            SELECT s
            FROM Student s
            WHERE s.classEntity.id = :classId
            AND s.rollNumber = :rollNumber
            """)
    Optional<Student> findByClassIdAndRollNumber(
            @Param("classId") Long classId,
            @Param("rollNumber") Integer rollNumber
    );

    // ============================================================
    // EXISTS CHECKS
    // ============================================================
    boolean existsByClassEntityIdAndRollNumber(Long classId, Integer rollNumber);
    boolean existsByStudentId(String studentId);

    // ============================================================
    // FETCH WITH RELATIONSHIPS
    // ============================================================
    @Query("SELECT s FROM Student s JOIN FETCH s.classEntity WHERE s.id = :studentId")
    Optional<Student> findByIdWithClass(@Param("studentId") Long studentId);

    @Query("SELECT s FROM Student s JOIN FETCH s.classEntity")
    List<Student> findAllWithClass();

    @Query("SELECT s FROM Student s JOIN FETCH s.user WHERE s.classEntity.id = :classId")
    List<Student> findByClassIdWithUser(@Param("classId") Long classId);

    // ============================================================
    // ACTIVE STUDENTS
    // ============================================================
    @Query("""
            SELECT s
            FROM Student s
            JOIN s.user u
            WHERE u.isActive = true
            """)
    List<Student> findAllActiveStudents();

    // ============================================================
    // DATE RANGE
    // ============================================================
    List<Student> findByDateOfBirthBetween(java.time.LocalDate startDate, java.time.LocalDate endDate);

    // ============================================================
    // STATISTICS
    // ============================================================
    @Query("SELECT s.classEntity.className, COUNT(s) FROM Student s GROUP BY s.classEntity.className")
    List<Object[]> countStudentsByClassName();

    @Query("SELECT s.classEntity, COUNT(s) FROM Student s GROUP BY s.classEntity")
    List<Object[]> countStudentsByClassEntity();

    // ============================================================
    // ADDITIONAL USEFUL QUERIES
    // ============================================================

    // Count all students
    @Query("SELECT COUNT(s) FROM Student s")
    long countAllStudents();

    // Get students by class name with user details
    @Query("SELECT s FROM Student s JOIN FETCH s.user WHERE s.class_ = :className")
    List<Student> findByClassNameWithUser(@Param("className") String className);

    // Get students by section with user details
    @Query("SELECT s FROM Student s JOIN FETCH s.user WHERE s.section = :section")
    List<Student> findBySectionWithUser(@Param("section") String section);


}