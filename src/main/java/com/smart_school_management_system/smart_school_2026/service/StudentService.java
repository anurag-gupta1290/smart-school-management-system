package com.smart_school_management_system.smart_school_2026.service;

import com.smart_school_management_system.smart_school_2026.entity.*;
import com.smart_school_management_system.smart_school_2026.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final SubmissionRepository submissionRepository;
    private final QuizResultRepository quizResultRepository;
    private final UserRepository userRepository;

    // ============================================================
    // GET STUDENTS
    // ============================================================

    public Student getStudentByUserId(Long userId) {
        try {
            Optional<Student> studentOpt = studentRepository.findByUserId(userId);
            if (studentOpt.isPresent()) {
                return studentOpt.get(); // ✅ Naya student mil gaya
            }
            throw new RuntimeException("Student not found for userId: " + userId);
        } catch (Exception e) {
            throw new RuntimeException("Student not found for userId: " + userId);
        }
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    public Optional<Student> getStudentByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> getStudentsByClass(Long classId) {
        return studentRepository.findByClassEntityId(classId);
    }

    public List<Student> getStudentsByClassName(String className) {
        return studentRepository.findByClassName(className);
    }

    public List<Student> getStudentsByClassAndSection(String className, String section) {
        return studentRepository.findByClassAndSection(className, section);
    }

    // ============================================================
    // COUNT METHODS
    // ============================================================

    public long countStudentsByClass(Long classId) {
        return studentRepository.countByClassId(classId);
    }

    public long countStudentsByClassName(String className) {
        return studentRepository.countByClass_(className);
    }

    public long countStudentsByClassAndSection(String className, String section) {
        return studentRepository.countByClassAndSection(className, section);
    }

    public long countStudents() {
        return studentRepository.countAllStudents();
    }

    // ============================================================
    // DASHBOARD
    // ============================================================

    public Map<String, Object> getStudentDashboard(Long userId) {
        Student student = getStudentByUserId(userId);
        Map<String, Object> dashboard = new HashMap<>();

        // 1. BASIC INFO
        dashboard.put("studentId", student.getStudentId() != null ? student.getStudentId() : "N/A");

        if (student.getUser() != null) {
            dashboard.put("fullName", student.getUser().getFullName() != null ? student.getUser().getFullName() : "Demo Student");
            dashboard.put("email", student.getUser().getEmail() != null ? student.getUser().getEmail() : "demo@student.com");
            dashboard.put("phone", student.getUser().getPhone() != null ? student.getUser().getPhone() : "N/A");
        } else {
            dashboard.put("fullName", "Demo Student");
            dashboard.put("email", "demo@student.com");
            dashboard.put("phone", "N/A");
        }

        String className = "N/A";
        if (student.getClassEntity() != null) {
            className = student.getClassEntity().getClassName();
        } else if (student.getClass_() != null) {
            className = student.getClass_();
        }
        dashboard.put("class", className);
        dashboard.put("section", student.getSection() != null ? student.getSection() : "N/A");
        dashboard.put("rollNumber", student.getRollNumber() != null ? student.getRollNumber() : "N/A");
        dashboard.put("guardianName", student.getGuardianName() != null ? student.getGuardianName() : "N/A");
        dashboard.put("guardianPhone", student.getGuardianPhone() != null ? student.getGuardianPhone() : "N/A");

        // 2. ATTENDANCE
        try {
            List<Attendance> attendances = attendanceRepository.findByStudentId(student.getId());
            long presentDays = attendances.stream()
                    .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                    .count();
            long totalDays = attendances.size();

            dashboard.put("totalPresent", presentDays);
            dashboard.put("totalDays", totalDays);
            double attendancePercentage = totalDays == 0 ? 0.0 :
                    Math.round((presentDays * 100.0 / totalDays) * 100.0) / 100.0;
            dashboard.put("attendancePercentage", attendancePercentage);
        } catch (Exception e) {
            dashboard.put("totalPresent", 0);
            dashboard.put("totalDays", 0);
            dashboard.put("attendancePercentage", 0.0);
        }

        // 3. ASSIGNMENTS
        try {
            List<Submission> submissions = submissionRepository.findByStudentId(student.getId());
            long totalAssignments = submissions.size();
            long submittedCount = submissions.stream()
                    .filter(s -> s.getStatus() == SubmissionStatus.SUBMITTED ||
                            s.getStatus() == SubmissionStatus.GRADED)
                    .count();
            long gradedCount = submissions.stream()
                    .filter(s -> s.getStatus() == SubmissionStatus.GRADED)
                    .count();

            dashboard.put("totalAssignments", totalAssignments);
            dashboard.put("submittedAssignments", submittedCount);
            dashboard.put("pendingAssignments", totalAssignments - submittedCount);
            dashboard.put("gradedAssignments", gradedCount);

            double avgMarks = submissions.stream()
                    .filter(s -> s.getMarksObtained() != null)
                    .mapToDouble(Submission::getMarksObtained)
                    .average()
                    .orElse(0.0);
            dashboard.put("averageMarks", Math.round(avgMarks * 100.0) / 100.0);
        } catch (Exception e) {
            dashboard.put("totalAssignments", 0);
            dashboard.put("submittedAssignments", 0);
            dashboard.put("pendingAssignments", 0);
            dashboard.put("gradedAssignments", 0);
            dashboard.put("averageMarks", 0.0);
        }

        // 4. QUIZZES
        try {
            List<QuizResult> quizResults = quizResultRepository.findByStudentId(student.getId());
            long totalQuizzes = quizResults.size();

            dashboard.put("totalQuizzes", totalQuizzes);
            dashboard.put("completedQuizzes", totalQuizzes);

            double avgQuizScore = quizResults.stream()
                    .filter(r -> r.getPercentage() != null)
                    .mapToDouble(r -> r.getPercentage().doubleValue())
                    .average()
                    .orElse(0.0);
            dashboard.put("averageQuizScore", Math.round(avgQuizScore * 100.0) / 100.0);

            String grade = calculateGrade(avgQuizScore);
            dashboard.put("grade", grade);
        } catch (Exception e) {
            dashboard.put("totalQuizzes", 0);
            dashboard.put("completedQuizzes", 0);
            dashboard.put("averageQuizScore", 0.0);
            dashboard.put("grade", "N/A");
        }

        // 5. CLASS TEACHER
        String classTeacher = "Not Assigned";
        if (student.getClassEntity() != null) {
            try {
                Teacher teacher = student.getClassEntity().getClassTeacher();
                if (teacher != null && teacher.getUser() != null) {
                    classTeacher = teacher.getUser().getFullName();
                }
            } catch (Exception e) {
                classTeacher = "Not Available";
            }
        }
        dashboard.put("classTeacher", classTeacher);

        // 6. RECENT SUBMISSIONS
        try {
            List<Submission> submissions = submissionRepository.findByStudentId(student.getId());
            List<Map<String, Object>> recentSubmissions = submissions.stream()
                    .sorted((s1, s2) -> {
                        if (s1.getSubmissionDate() == null) return 1;
                        if (s2.getSubmissionDate() == null) return -1;
                        return s2.getSubmissionDate().compareTo(s1.getSubmissionDate());
                    })
                    .limit(5)
                    .map(s -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", s.getId());
                        map.put("title", s.getAssignment() != null ? s.getAssignment().getTitle() : "N/A");
                        map.put("status", s.getStatus() != null ? s.getStatus().toString() : "PENDING");
                        map.put("marks", s.getMarksObtained() != null ? s.getMarksObtained() : "Not Graded");
                        map.put("date", s.getSubmissionDate() != null ? s.getSubmissionDate().toString() : "N/A");
                        return map;
                    })
                    .collect(Collectors.toList());
            dashboard.put("recentSubmissions", recentSubmissions);
        } catch (Exception e) {
            dashboard.put("recentSubmissions", new ArrayList<>());
        }

        // 7. PERFORMANCE
        Map<String, Object> performance = new HashMap<>();
        performance.put("attendance", dashboard.get("attendancePercentage"));
        performance.put("averageMarks", dashboard.get("averageMarks"));
        performance.put("averageQuizScore", dashboard.get("averageQuizScore"));
        performance.put("grade", dashboard.get("grade"));
        performance.put("submissionsRate", dashboard.get("totalAssignments") != null && (int)dashboard.get("totalAssignments") > 0 ?
                Math.round(((int)dashboard.get("submittedAssignments") * 100.0 / (int)dashboard.get("totalAssignments")) * 100.0) / 100.0 : 0);
        dashboard.put("performance", performance);

        dashboard.put("courses", 6);

        return dashboard;
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private String calculateGrade(double percentage) {
        if (percentage >= 90) return "A+";
        else if (percentage >= 80) return "A";
        else if (percentage >= 70) return "B";
        else if (percentage >= 60) return "C";
        else if (percentage >= 50) return "D";
        else if (percentage >= 40) return "E";
        else return "F";
    }

    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public Student updateStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student getStudentWithDetails(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    public List<Student> searchStudents(String keyword) {
        return studentRepository.searchByName(keyword);
    }

    public List<Student> getActiveStudents() {
        return studentRepository.findAllActiveStudents();
    }

    public Map<String, Long> getStudentCountByClass() {
        List<Object[]> results = studentRepository.countStudentsByClassName();
        Map<String, Long> countMap = new HashMap<>();
        for (Object[] result : results) {
            countMap.put((String) result[0], (Long) result[1]);
        }
        return countMap;
    }

    @Transactional(readOnly = true)
    public Long getCurrentStudentId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Student student = studentRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            return student.getId();
        } catch (Exception e) {
            throw new RuntimeException("Student not found for current user");
        }
    }
}