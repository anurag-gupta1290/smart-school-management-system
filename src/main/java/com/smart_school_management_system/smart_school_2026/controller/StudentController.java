package com.smart_school_management_system.smart_school_2026.controller;

import com.smart_school_management_system.smart_school_2026.entity.*;
import com.smart_school_management_system.smart_school_2026.repository.*;
import com.smart_school_management_system.smart_school_2026.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Transactional
public class StudentController {

    private final StudentService studentService;
    private final AssignmentRepository assignmentRepository;
    private final QuizRepository quizRepository;
    private final SubmissionRepository submissionRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final QuizResultRepository quizResultRepository;
    private final ClassEntityRepository classEntityRepository;

    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<?> getStudentDashboard(@PathVariable Long userId) {
        try {
            Map<String, Object> dashboard = studentService.getStudentDashboard(userId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getStudentProfile(@PathVariable Long userId) {
        try {
            Student student = studentService.getStudentByUserId(userId);

            // ✅ DTO Return karo (JSON Serialization error fix)
            Map<String, Object> studentDTO = new HashMap<>();
            studentDTO.put("id", student.getId());
            studentDTO.put("studentId", student.getStudentId());
            studentDTO.put("class_", student.getClass_());
            studentDTO.put("section", student.getSection());
            studentDTO.put("rollNumber", student.getRollNumber());
            studentDTO.put("guardianName", student.getGuardianName());
            studentDTO.put("guardianPhone", student.getGuardianPhone());
            if (student.getUser() != null) {
                studentDTO.put("userId", student.getUser().getId());
                studentDTO.put("fullName", student.getUser().getFullName());
                studentDTO.put("email", student.getUser().getEmail());
            }
            return ResponseEntity.ok(studentDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Student not found for userId: " + userId));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<?> getStudentsByClass(@PathVariable Long classId) {
        try {
            List<Student> students = studentService.getStudentsByClass(classId);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // ✅ ASSIGNMENTS - GET ALL (Sabhi Students Ko Dikhenge)
    // ============================================================
    @GetMapping("/assignments/{studentId}")
    public ResponseEntity<?> getAssignmentsForStudent(@PathVariable Long studentId) {
        try {
            // ✅ Ab class match nahi karenge - saare assignments dikhenge
            List<Assignment> assignments = assignmentRepository.findAll();
            List<Map<String, Object>> result = new ArrayList<>();

            for (Assignment a : assignments) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", a.getId());
                map.put("title", a.getTitle());
                map.put("description", a.getDescription());
                map.put("dueDate", a.getDueDate() != null ? a.getDueDate().toString() : "");
                map.put("maxMarks", a.getMaxMarks());
                map.put("className", a.getClassEntity() != null ? a.getClassEntity().getClassName() : "N/A");
                map.put("subjectName", a.getSubject() != null ? a.getSubject().getSubjectName() : "N/A");
                map.put("teacherName", a.getTeacher() != null && a.getTeacher().getUser() != null ? a.getTeacher().getUser().getFullName() : "N/A");

                boolean submitted = submissionRepository
                        .findByAssignmentIdAndStudentId(a.getId(), studentId)
                        .isPresent();
                map.put("submitted", submitted);

                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    // ============================================================
    // ✅ QUIZZES - GET ALL (Sabhi Students Ko Dikhenge)
    // ============================================================
    @GetMapping("/quizzes/{studentId}")
    public ResponseEntity<?> getQuizzesForStudent(@PathVariable Long studentId) {
        try {
            // ✅ Ab class match nahi karenge - saare quizzes dikhenge
            List<Quiz> quizzes = quizRepository.findAll();
            List<Map<String, Object>> result = new ArrayList<>();

            for (Quiz q : quizzes) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", q.getId());
                map.put("title", q.getTitle());
                map.put("description", q.getDescription());
                map.put("quizDate", q.getQuizDate() != null ? q.getQuizDate().toString() : "");
                map.put("duration", q.getDuration());
                map.put("maxMarks", q.getMaxMarks());
                map.put("className", q.getClassEntity() != null ? q.getClassEntity().getClassName() : "N/A");
                map.put("subjectName", q.getSubject() != null ? q.getSubject().getSubjectName() : "N/A");
                map.put("teacherName", q.getTeacher() != null && q.getTeacher().getUser() != null ? q.getTeacher().getUser().getFullName() : "N/A");

                boolean attempted = quizResultRepository
                        .findByQuizIdAndStudentId(q.getId(), studentId)
                        .isPresent();
                map.put("attempted", attempted);

                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    // ============================================================
    // ✅ ASSIGNMENT SUBMIT
    // ============================================================
    @PostMapping("/assignments/{assignmentId}/submit")
    public ResponseEntity<?> submitAssignment(@PathVariable Long assignmentId, @RequestBody Map<String, Object> request) {
        try {
            // ✅ Student ID ko request se lo (SecurityContextHolder se nahi)
            Long studentId = null;

            // Pehle request se try karo
            if (request.get("studentId") != null) {
                studentId = Long.parseLong(request.get("studentId").toString());
            }

            // Agar request mein nahi hai, toh getCurrentStudentId() se try karo
            if (studentId == null) {
                try {
                    studentId = studentService.getCurrentStudentId();
                } catch (Exception ex) {
                    // Log the error but continue
                    System.out.println("⚠️ Could not get student ID from security context: " + ex.getMessage());
                }
            }

            String fileUrl = (String) request.get("fileUrl");
            String comments = (String) request.get("comments");

            if (studentId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Student ID is required"));
            }

            boolean alreadySubmitted = submissionRepository
                    .findByAssignmentIdAndStudentId(assignmentId, studentId)
                    .isPresent();

            if (alreadySubmitted) {
                return ResponseEntity.badRequest().body(Map.of("error", "Already submitted"));
            }

            Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
            Student student = studentRepository.findById(studentId).orElse(null);

            if (assignment == null || student == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Assignment or Student not found"));
            }

            Submission submission = new Submission();
            submission.setAssignment(assignment);
            submission.setStudent(student);
            submission.setSubmissionDate(LocalDateTime.now());
            submission.setFileUrl(fileUrl);
            submission.setComments(comments);
            submission.setStatus(SubmissionStatus.SUBMITTED);

            Submission saved = submissionRepository.save(submission);

            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("message", "Assignment submitted successfully");
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // ✅ QUIZ SUBMIT - Student submits quiz answers
    // ============================================================
    @PostMapping("/quizzes/{quizId}/submit")
    public ResponseEntity<?> submitQuiz(@PathVariable Long quizId, @RequestBody Map<String, Object> request) {
        try {
            Long studentId = request.get("studentId") != null ? Long.parseLong(request.get("studentId").toString()) : null;
            Integer marksObtained = request.get("marksObtained") != null ?
                    Integer.parseInt(request.get("marksObtained").toString()) : null;

            if (studentId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Student ID is required"));
            }
            if (marksObtained == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Marks are required"));
            }

            // Check if already attempted
            boolean alreadyAttempted = quizResultRepository
                    .findByQuizIdAndStudentId(quizId, studentId)
                    .isPresent();

            if (alreadyAttempted) {
                return ResponseEntity.badRequest().body(Map.of("error", "Already attempted this quiz"));
            }

            Quiz quiz = quizRepository.findById(quizId).orElse(null);
            Student student = studentRepository.findById(studentId).orElse(null);

            if (quiz == null || student == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Quiz or Student not found"));
            }

            // Calculate percentage and grade
            double percentage = (marksObtained * 100.0) / quiz.getMaxMarks();
            String grade = calculateGrade(percentage);

            QuizResult quizResult = new QuizResult();
            quizResult.setQuiz(quiz);
            quizResult.setStudent(student);
            quizResult.setMarksObtained(marksObtained);
            quizResult.setTotalMarks(quiz.getMaxMarks());
            quizResult.setPercentage(java.math.BigDecimal.valueOf(Math.round(percentage * 100.0) / 100.0));
            quizResult.setGrade(grade);
            quizResult.setAttemptDate(LocalDateTime.now());

            QuizResult saved = quizResultRepository.save(quizResult);

            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("message", "Quiz submitted successfully!");
            response.put("percentage", saved.getPercentage());
            response.put("grade", saved.getGrade());
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // ✅ QUIZ RESULTS - GET STUDENT QUIZ RESULTS
    // ============================================================
    @GetMapping("/quiz-results/{studentId}")
    public ResponseEntity<?> getQuizResults(@PathVariable Long studentId) {
        try {
            List<QuizResult> results = quizResultRepository.findByStudentId(studentId);
            List<Map<String, Object>> result = new ArrayList<>();

            for (QuizResult qr : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", qr.getId());
                map.put("quizId", qr.getQuiz() != null ? qr.getQuiz().getId() : null);
                map.put("quizTitle", qr.getQuiz() != null ? qr.getQuiz().getTitle() : "N/A");
                map.put("subjectName", qr.getQuiz() != null && qr.getQuiz().getSubject() != null ?
                        qr.getQuiz().getSubject().getSubjectName() : "N/A");
                map.put("marksObtained", qr.getMarksObtained());
                map.put("totalMarks", qr.getTotalMarks());
                map.put("percentage", qr.getPercentage());
                map.put("grade", qr.getGrade());
                map.put("attemptDate", qr.getAttemptDate());
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    // ============================================================
    // ✅ GET STUDENT SUBMISSIONS
    // ============================================================
    @GetMapping("/submissions/{studentId}")
    public ResponseEntity<?> getStudentSubmissions(@PathVariable Long studentId) {
        try {
            List<Submission> submissions = submissionRepository.findByStudentId(studentId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (Submission s : submissions) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", s.getId());
                map.put("assignmentId", s.getAssignment() != null ? s.getAssignment().getId() : null);
                map.put("assignmentTitle", s.getAssignment() != null ? s.getAssignment().getTitle() : "N/A");
                map.put("status", s.getStatus());
                map.put("marksObtained", s.getMarksObtained());
                map.put("submissionDate", s.getSubmissionDate());
                map.put("feedback", s.getFeedback());
                map.put("fileUrl", s.getFileUrl());
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // ✅ GET STUDENT RESULTS (Assignments)
    // ============================================================
    @GetMapping("/results/{studentId}")
    public ResponseEntity<?> getStudentResults(@PathVariable Long studentId) {
        try {
            List<Submission> submissions = submissionRepository.findByStudentId(studentId);

            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> subjectWiseMarks = new ArrayList<>();
            int totalMarks = 0;
            int totalMaxMarks = 0;  // ✅ Add this
            int gradedCount = 0;

            for (Submission s : submissions) {
                if (s.getStatus() == SubmissionStatus.GRADED && s.getMarksObtained() != null) {
                    Map<String, Object> mark = new HashMap<>();
                    mark.put("assignmentId", s.getAssignment() != null ? s.getAssignment().getId() : null);
                    mark.put("assignmentTitle", s.getAssignment() != null ? s.getAssignment().getTitle() : "N/A");
                    mark.put("subjectName", s.getAssignment() != null && s.getAssignment().getSubject() != null ?
                            s.getAssignment().getSubject().getSubjectName() : "N/A");
                    mark.put("marksObtained", s.getMarksObtained());
                    mark.put("maxMarks", s.getAssignment() != null ? s.getAssignment().getMaxMarks() : 100);
                    mark.put("percentage", s.getAssignment() != null ?
                            (s.getMarksObtained() * 100.0) / s.getAssignment().getMaxMarks() : 0);
                    mark.put("feedback", s.getFeedback());
                    mark.put("submissionDate", s.getSubmissionDate());

                    subjectWiseMarks.add(mark);
                    totalMarks += s.getMarksObtained();
                    totalMaxMarks += s.getAssignment() != null ? s.getAssignment().getMaxMarks() : 100;  // ✅ Add this
                    gradedCount++;
                }
            }

            // ✅ Correct overall percentage calculation
            double overallPercentage = totalMaxMarks > 0 ?
                    (totalMarks * 100.0) / totalMaxMarks : 0;

            Map<String, Object> performance = new HashMap<>();
            performance.put("totalAssignments", submissions.size());
            performance.put("gradedCount", gradedCount);
            performance.put("pendingCount", submissions.size() - gradedCount);
            performance.put("averageMarks", totalMaxMarks > 0 ? Math.round((totalMarks * 100.0) / totalMaxMarks * 100.0) / 100.0 : 0);
            performance.put("overallPercentage", Math.round(overallPercentage * 100.0) / 100.0);
            performance.put("grade", calculateGrade(overallPercentage));

            result.put("performance", performance);
            result.put("subjectWiseMarks", subjectWiseMarks);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // ✅ HELPER METHOD - CALCULATE GRADE
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

    @GetMapping("/attendance/{studentId}")
    public ResponseEntity<?> getStudentAttendance(@PathVariable Long studentId) {
        try {
            List<Attendance> attendances = attendanceRepository.findByStudentId(studentId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (Attendance a : attendances) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", a.getId());
                map.put("date", a.getAttendanceDate() != null ? a.getAttendanceDate().toString() : "N/A");
                map.put("status", a.getStatus().toString());
                map.put("subjectName", a.getSubject() != null ? a.getSubject().getSubjectName() : "N/A");
                map.put("className", a.getClassEntity() != null ? a.getClassEntity().getClassName() : "N/A");
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}