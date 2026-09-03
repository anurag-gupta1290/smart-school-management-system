package com.smart_school_management_system.smart_school_2026.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smart_school_management_system.smart_school_2026.entity.ClassEntity;
import com.smart_school_management_system.smart_school_2026.entity.Role;
import com.smart_school_management_system.smart_school_2026.entity.Student;
import com.smart_school_management_system.smart_school_2026.entity.Subject;
import com.smart_school_management_system.smart_school_2026.entity.Teacher;
import com.smart_school_management_system.smart_school_2026.entity.User;
import com.smart_school_management_system.smart_school_2026.repository.ClassEntityRepository;
import com.smart_school_management_system.smart_school_2026.repository.StudentRepository;
import com.smart_school_management_system.smart_school_2026.repository.SubjectRepository;
import com.smart_school_management_system.smart_school_2026.repository.TeacherRepository;
import com.smart_school_management_system.smart_school_2026.repository.UserRepository;
import com.smart_school_management_system.smart_school_2026.service.AdminService;
import com.smart_school_management_system.smart_school_2026.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://localhost:8080",
        "http://localhost:8083",
        "http://localhost:8084",
        "http://localhost:5500",
        "http://127.0.0.1:5500"
})
@Transactional
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final ClassEntityRepository classEntityRepository;
    private final PasswordEncoder passwordEncoder;

    // ============================================================
    // DASHBOARD
    // ============================================================
    @GetMapping("/dashboard")
    public ResponseEntity<?> getAdminDashboard() {
        try {
            Map<String, Object> dashboard = adminService.getAdminDashboard();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // USER MANAGEMENT
    // ============================================================
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            List<User> users = userService.getUsersByRole(Role.valueOf(role.toUpperCase()));
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role. Valid roles: ADMIN, TEACHER, STUDENT"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsers(@RequestParam String query) {
        try {
            List<User> users = userService.searchUsers(query);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users/statistics")
    public ResponseEntity<?> getUserStatistics() {
        try {
            Map<String, Object> stats = userService.getUserStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/users/{userId}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long userId) {
        try {
            userService.activateUser(userId);
            return ResponseEntity.ok(Map.of("message", "User activated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/users/{userId}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long userId) {
        try {
            userService.deactivateUser(userId);
            return ResponseEntity.ok(Map.of("message", "User deactivated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/users/{userId}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            String newPassword = request.get("newPassword");
            if (newPassword == null || newPassword.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "New password is required"));
            }
            userService.changePassword(userId, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // 🔥🔥🔥 SUBJECTS API - ADD THIS 🔥🔥🔥
    // ============================================================
    @GetMapping("/subjects")
    public ResponseEntity<?> getAllSubjects() {
        try {
            List<Subject> subjects = subjectRepository.findAll();
            System.out.println("✅ Subjects found: " + subjects.size());

            // Map to simple DTO
            List<Map<String, Object>> result = new ArrayList<>();
            for (Subject s : subjects) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", s.getId());
                map.put("subjectName", s.getSubjectName());
                map.put("subjectCode", s.getSubjectCode());
                map.put("description", s.getDescription() != null ? s.getDescription() : "");
                map.put("credits", s.getCredits() != null ? s.getCredits() : 0);
                map.put("videoUrl", s.getVideoUrl());
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // COURSES (SUBJECTS) CRUD
    // ============================================================
    @GetMapping("/courses")
    public ResponseEntity<?> getAllCourses() {
        try {
            List<Subject> subjects = adminService.getAllCourses();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Subject s : subjects) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", s.getId());
                map.put("subjectName", s.getSubjectName());
                map.put("subjectCode", s.getSubjectCode());
                map.put("description", s.getDescription() != null ? s.getDescription() : "");
                map.put("credits", s.getCredits() != null ? s.getCredits() : 0);
                map.put("videoUrl", s.getVideoUrl());
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        try {
            Subject subject = adminService.getCourseById(id);
            return ResponseEntity.ok(subject);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/courses")
    public ResponseEntity<?> createCourse(@RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("subjectName");
            String code = (String) request.get("subjectCode");
            Integer credits = request.get("credits") != null ?
                    Integer.parseInt(request.get("credits").toString()) : 4;
            String description = (String) request.get("description");
            String videoUrl = (String) request.get("videoUrl");

            if (name == null || name.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Subject name is required"));
            }
            if (code == null || code.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Subject code is required"));
            }

            if (subjectRepository.existsBySubjectCode(code)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Subject code already exists"));
            }

            Subject subject = new Subject();
            subject.setSubjectName(name);
            subject.setSubjectCode(code);
            subject.setCredits(credits);
            subject.setDescription(description);
            subject.setVideoUrl(videoUrl);

            Subject saved = adminService.createCourse(subject);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("subjectName");
            String code = (String) request.get("subjectCode");
            Integer credits = request.get("credits") != null ?
                    Integer.parseInt(request.get("credits").toString()) : 4;
            String description = (String) request.get("description");
            String videoUrl = (String) request.get("videoUrl");

            Subject subjectDetails = new Subject();
            subjectDetails.setSubjectName(name);
            subjectDetails.setSubjectCode(code);
            subjectDetails.setCredits(credits);
            subjectDetails.setDescription(description);
            subjectDetails.setVideoUrl(videoUrl);

            Subject updated = adminService.updateCourse(id, subjectDetails);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        try {
            adminService.deleteCourse(id);
            return ResponseEntity.ok(Map.of("message", "Course deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // TEACHERS CRUD
    // ============================================================
    @GetMapping("/teachers")
    public ResponseEntity<?> getAllTeachers() {
        try {
            List<Teacher> teachers = adminService.getAllTeachers();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Teacher t : teachers) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", t.getId());
                map.put("teacherId", t.getTeacherId());
                map.put("fullName", t.getUser() != null ? t.getUser().getFullName() : "");
                map.put("email", t.getUser() != null ? t.getUser().getEmail() : "");
                map.put("department", t.getDepartment() != null ? t.getDepartment() : "");
                map.put("qualification", t.getQualification() != null ? t.getQualification() : "");
                map.put("experienceYears", t.getExperienceYears() != null ? t.getExperienceYears() : 0);
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/teachers/{id}")
    public ResponseEntity<?> getTeacherById(@PathVariable Long id) {
        try {
            Teacher teacher = adminService.getTeacherById(id);
            return ResponseEntity.ok(teacher);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/teachers")
    public ResponseEntity<?> createTeacher(@RequestBody Map<String, Object> request) {
        try {
            String fullName = (String) request.get("fullName");
            String email = (String) request.get("email");
            String teacherId = (String) request.get("teacherId");
            String department = (String) request.get("department");
            String qualification = (String) request.get("qualification");
            Integer experienceYears = request.get("experienceYears") != null ?
                    Integer.parseInt(request.get("experienceYears").toString()) : 0;

            if (fullName == null || fullName.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Full name is required"));
            }
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }

            if (userRepository.existsByEmail(email)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
            }

            User user = new User();
            user.setUsername(email);
            user.setEmail(email);
            user.setFullName(fullName);
            user.setPassword(passwordEncoder.encode("teacher123"));
            user.setRole(Role.TEACHER);
            user.setIsActive(true);
            User savedUser = userRepository.save(user);

            Teacher teacher = new Teacher();
            teacher.setUser(savedUser);
            teacher.setTeacherId(teacherId != null ? teacherId : "TCH-" + String.format("%03d", savedUser.getId()));
            teacher.setDepartment(department);
            teacher.setQualification(qualification);
            teacher.setExperienceYears(experienceYears);

            Teacher saved = adminService.createTeacher(teacher);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/teachers/{id}")
    public ResponseEntity<?> updateTeacher(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            String department = (String) request.get("department");
            String qualification = (String) request.get("qualification");
            Integer experienceYears = request.get("experienceYears") != null ?
                    Integer.parseInt(request.get("experienceYears").toString()) : 0;

            Teacher teacherDetails = new Teacher();
            teacherDetails.setDepartment(department);
            teacherDetails.setQualification(qualification);
            teacherDetails.setExperienceYears(experienceYears);

            Teacher updated = adminService.updateTeacher(id, teacherDetails);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/teachers/{id}")
    public ResponseEntity<?> deleteTeacher(@PathVariable Long id) {
        try {
            adminService.deleteTeacher(id);
            return ResponseEntity.ok(Map.of("message", "Teacher deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // STUDENTS CRUD
    // ============================================================
    @GetMapping("/students")
    public ResponseEntity<?> getAllStudents() {
        try {
            List<Student> students = adminService.getAllStudents();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Student s : students) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", s.getId());
                map.put("studentId", s.getStudentId());
                map.put("fullName", s.getUser() != null ? s.getUser().getFullName() : "");
                map.put("email", s.getUser() != null ? s.getUser().getEmail() : "");
                map.put("class", s.getClass_() != null ? s.getClass_() : "");
                map.put("section", s.getSection() != null ? s.getSection() : "");
                map.put("rollNumber", s.getRollNumber() != null ? s.getRollNumber() : 0);
                map.put("guardianName", s.getGuardianName() != null ? s.getGuardianName() : "");
                map.put("guardianPhone", s.getGuardianPhone() != null ? s.getGuardianPhone() : "");
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        try {
            Student student = adminService.getStudentById(id);
            return ResponseEntity.ok(student);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/students")
    public ResponseEntity<?> createStudent(@RequestBody Map<String, Object> request) {
        try {
            String fullName = (String) request.get("fullName");
            String email = (String) request.get("email");
            String studentId = (String) request.get("studentId");
            String cls = (String) request.get("class");
            String guardianName = (String) request.get("guardianName");
            String guardianPhone = (String) request.get("guardianPhone");

            if (fullName == null || fullName.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Full name is required"));
            }
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }

            if (userRepository.existsByEmail(email)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
            }

            User user = new User();
            user.setUsername(email);
            user.setEmail(email);
            user.setFullName(fullName);
            user.setPassword(passwordEncoder.encode("student123"));
            user.setRole(Role.STUDENT);
            user.setIsActive(true);
            User savedUser = userRepository.save(user);

            Student student = new Student();
            student.setUser(savedUser);
            student.setStudentId(studentId != null ? studentId : "STU-" + String.format("%03d", savedUser.getId()));
            student.setClass_(cls != null ? cls : "10-A");
            student.setGuardianName(guardianName);
            student.setGuardianPhone(guardianPhone);

            Student saved = adminService.createStudent(student);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            String cls = (String) request.get("class");
            String section = (String) request.get("section");
            Integer rollNumber = request.get("rollNumber") != null ?
                    Integer.parseInt(request.get("rollNumber").toString()) : null;
            String guardianName = (String) request.get("guardianName");
            String guardianPhone = (String) request.get("guardianPhone");

            Student studentDetails = new Student();
            studentDetails.setClass_(cls);
            studentDetails.setSection(section);
            studentDetails.setRollNumber(rollNumber);
            studentDetails.setGuardianName(guardianName);
            studentDetails.setGuardianPhone(guardianPhone);

            Student updated = adminService.updateStudent(id, studentDetails);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        try {
            adminService.deleteStudent(id);
            return ResponseEntity.ok(Map.of("message", "Student deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // CLASSES CRUD
    // ============================================================
    @GetMapping("/classes")
    public ResponseEntity<?> getAllClasses() {
        try {
            List<ClassEntity> classes = adminService.getAllClasses();
            List<Map<String, Object>> result = new ArrayList<>();
            for (ClassEntity c : classes) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", c.getId());
                map.put("className", c.getClassName());
                map.put("section", c.getSection() != null ? c.getSection() : "");
                map.put("roomNumber", c.getRoomNumber() != null ? c.getRoomNumber() : "");
                map.put("capacity", c.getCapacity() != null ? c.getCapacity() : 0);
                map.put("academicYear", c.getAcademicYear() != null ? c.getAcademicYear() : "");
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/classes/{id}")
    public ResponseEntity<?> getClassById(@PathVariable Long id) {
        try {
            ClassEntity cls = adminService.getClassById(id);
            return ResponseEntity.ok(cls);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/classes")
    public ResponseEntity<?> createClass(@RequestBody Map<String, Object> request) {
        try {
            String className = (String) request.get("className");
            String section = (String) request.get("section");
            String roomNumber = (String) request.get("roomNumber");
            Integer capacity = request.get("capacity") != null ?
                    Integer.parseInt(request.get("capacity").toString()) : 30;

            if (className == null || className.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Class name is required"));
            }

            ClassEntity cls = new ClassEntity();
            cls.setClassName(className);
            cls.setSection(section);
            cls.setRoomNumber(roomNumber);
            cls.setCapacity(capacity);
            cls.setAcademicYear("2026-27");

            ClassEntity saved = adminService.createClass(cls);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/classes/{id}")
    public ResponseEntity<?> updateClass(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            String className = (String) request.get("className");
            String section = (String) request.get("section");
            String roomNumber = (String) request.get("roomNumber");
            Integer capacity = request.get("capacity") != null ?
                    Integer.parseInt(request.get("capacity").toString()) : 30;

            ClassEntity classDetails = new ClassEntity();
            classDetails.setClassName(className);
            classDetails.setSection(section);
            classDetails.setRoomNumber(roomNumber);
            classDetails.setCapacity(capacity);

            ClassEntity updated = adminService.updateClass(id, classDetails);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/classes/{id}")
    public ResponseEntity<?> deleteClass(@PathVariable Long id) {
        try {
            adminService.deleteClass(id);
            return ResponseEntity.ok(Map.of("message", "Class deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}