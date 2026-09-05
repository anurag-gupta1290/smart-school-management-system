package com.smart_school_management_system.smart_school_2026.controller;

import com.smart_school_management_system.smart_school_2026.entity.*;
import com.smart_school_management_system.smart_school_2026.repository.*;
import com.smart_school_management_system.smart_school_2026.service.NoteService;
import com.smart_school_management_system.smart_school_2026.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Transactional
public class TeacherController {

    private final TeacherService teacherService;
    private final AssignmentRepository assignmentRepository;
    private final QuizRepository quizRepository;
    private final ClassEntityRepository classEntityRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final SubmissionRepository submissionRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final PasswordEncoder passwordEncoder;
    private final NoteService noteService;
    private final NoteRepository noteRepository;

    // ============================================================
    // ✅ PROFILE & DASHBOARD
    // ============================================================

    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<?> getTeacherDashboard(@PathVariable Long userId) {
        try {
            Map<String, Object> dashboard = teacherService.getTeacherDashboard(userId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getTeacherProfile(@PathVariable Long userId) {
        try {
            Teacher teacher = teacherService.getTeacherByUserId(userId);

            Map<String, Object> teacherDTO = new HashMap<>();
            teacherDTO.put("id", teacher.getId());
            teacherDTO.put("teacherId", teacher.getTeacherId());
            teacherDTO.put("department", teacher.getDepartment());
            teacherDTO.put("qualification", teacher.getQualification());
            teacherDTO.put("experienceYears", teacher.getExperienceYears());
            teacherDTO.put("joiningDate", teacher.getJoiningDate());

            if (teacher.getUser() != null) {
                teacherDTO.put("userId", teacher.getUser().getId());
                teacherDTO.put("fullName", teacher.getUser().getFullName());
                teacherDTO.put("email", teacher.getUser().getEmail());
            }
            return ResponseEntity.ok(teacherDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Teacher not found for userId: " + userId));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTeachers() {
        try {
            List<Teacher> teachers = teacherService.getAllTeachers();
            return ResponseEntity.ok(teachers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<?> getTeachersByDepartment(@PathVariable String department) {
        try {
            List<Teacher> teachers = teacherService.getTeachersByDepartment(department);
            return ResponseEntity.ok(teachers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // ✅ ASSIGNMENTS
    // ============================================================

    @GetMapping("/assignments")
    public ResponseEntity<?> getAllAssignments() {
        try {
            List<Assignment> assignments = assignmentRepository.findAll();

            List<Map<String, Object>> result = new ArrayList<>();
            for (Assignment a : assignments) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", a.getId());
                map.put("title", a.getTitle());
                map.put("description", a.getDescription());
                map.put("dueDate", a.getDueDate() != null ? a.getDueDate().toString() : "");
                map.put("maxMarks", a.getMaxMarks());
                map.put("className", a.getClassEntity() != null ? a.getClassEntity().getClassName() : "");
                map.put("subjectName", a.getSubject() != null ? a.getSubject().getSubjectName() : "");
                map.put("teacherName", a.getTeacher() != null ? a.getTeacher().getUser().getFullName() : "");
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/assignments")
    public ResponseEntity<?> createAssignment(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("📝 Received assignment request: " + request);

            String title = (String) request.get("title");
            String description = (String) request.get("description");
            String dueDateStr = (String) request.get("dueDate");
            Integer maxMarks = request.get("maxMarks") != null ? Integer.parseInt(request.get("maxMarks").toString()) : 100;
            Long classId = request.get("classId") != null ? Long.parseLong(request.get("classId").toString()) : null;
            Long subjectId = request.get("subjectId") != null ? Long.parseLong(request.get("subjectId").toString()) : null;
            Long teacherId = request.get("teacherId") != null ? Long.parseLong(request.get("teacherId").toString()) : null;

            if (title == null || title.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Title is required");
                error.put("success", false);
                return ResponseEntity.badRequest().body(error);
            }
            if (dueDateStr == null || dueDateStr.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Due date is required");
                error.put("success", false);
                return ResponseEntity.badRequest().body(error);
            }

            LocalDateTime dueDate;
            try {
                dueDate = LocalDateTime.parse(dueDateStr);
            } catch (Exception e1) {
                try {
                    dueDate = LocalDateTime.parse(dueDateStr.replace(" ", "T"));
                } catch (Exception e2) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        dueDate = LocalDateTime.parse(dueDateStr, formatter);
                    } catch (Exception e3) {
                        dueDate = LocalDateTime.parse(dueDateStr.replace("Z", ""));
                    }
                }
            }

            Teacher teacher = null;
            if (teacherId != null) {
                teacher = teacherRepository.findById(teacherId).orElse(null);
            }
            if (teacher == null) {
                List<Teacher> teachers = teacherRepository.findAll();
                if (!teachers.isEmpty()) {
                    teacher = teachers.get(0);
                } else {
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", "No teacher found");
                    error.put("success", false);
                    return ResponseEntity.badRequest().body(error);
                }
            }

            if (classId == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Class is required");
                error.put("success", false);
                return ResponseEntity.badRequest().body(error);
            }
            var classEntity = classEntityRepository.findById(classId).orElse(null);
            if (classEntity == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Class not found");
                error.put("success", false);
                return ResponseEntity.badRequest().body(error);
            }

            if (subjectId == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Subject is required");
                error.put("success", false);
                return ResponseEntity.badRequest().body(error);
            }
            var subject = subjectRepository.findById(subjectId).orElse(null);
            if (subject == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Subject not found");
                error.put("success", false);
                return ResponseEntity.badRequest().body(error);
            }

            Assignment assignment = new Assignment();
            assignment.setTitle(title);
            assignment.setDescription(description);
            assignment.setDueDate(dueDate);
            assignment.setMaxMarks(maxMarks);
            assignment.setClassEntity(classEntity);
            assignment.setSubject(subject);
            assignment.setTeacher(teacher);

            Assignment saved = assignmentRepository.save(assignment);

            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("title", saved.getTitle());
            response.put("message", "Assignment created successfully");
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ============================================================
    // ✅ QUIZZES
    // ============================================================

    @GetMapping("/quizzes")
    public ResponseEntity<?> getAllQuizzes() {
        try {
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
                map.put("className", q.getClassEntity() != null ? q.getClassEntity().getClassName() : "");
                map.put("subjectName", q.getSubject() != null ? q.getSubject().getSubjectName() : "");
                map.put("teacherName", q.getTeacher() != null ? q.getTeacher().getUser().getFullName() : "");
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/quizzes")
    public ResponseEntity<?> createQuiz(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("📝 Received quiz request: " + request);

            String title = (String) request.get("title");
            String description = (String) request.get("description");
            String quizDateStr = (String) request.get("quizDate");
            Integer duration = request.get("duration") != null ? Integer.parseInt(request.get("duration").toString()) : 30;
            Integer maxMarks = request.get("maxMarks") != null ? Integer.parseInt(request.get("maxMarks").toString()) : 100;
            Long classId = request.get("classId") != null ? Long.parseLong(request.get("classId").toString()) : null;
            Long subjectId = request.get("subjectId") != null ? Long.parseLong(request.get("subjectId").toString()) : null;
            Long teacherId = request.get("teacherId") != null ? Long.parseLong(request.get("teacherId").toString()) : null;

            List<Map<String, Object>> questions = (List<Map<String, Object>>) request.get("questions");

            if (title == null || title.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Title is required"));
            }
            if (quizDateStr == null || quizDateStr.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Quiz date is required"));
            }

            LocalDateTime quizDate;
            try {
                quizDate = LocalDateTime.parse(quizDateStr);
            } catch (Exception e1) {
                try {
                    quizDate = LocalDateTime.parse(quizDateStr.replace(" ", "T"));
                } catch (Exception e2) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    quizDate = LocalDateTime.parse(quizDateStr, formatter);
                }
            }

            Teacher teacher = null;
            if (teacherId != null) {
                teacher = teacherRepository.findById(teacherId).orElse(null);
            }
            if (teacher == null) {
                List<Teacher> teachers = teacherRepository.findAll();
                if (!teachers.isEmpty()) {
                    teacher = teachers.get(0);
                } else {
                    return ResponseEntity.badRequest().body(Map.of("error", "No teacher found"));
                }
            }

            if (classId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Class is required"));
            }
            var classEntity = classEntityRepository.findById(classId).orElse(null);
            if (classEntity == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Class not found"));
            }

            if (subjectId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Subject is required"));
            }
            var subject = subjectRepository.findById(subjectId).orElse(null);
            if (subject == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Subject not found"));
            }

            Quiz quiz = new Quiz();
            quiz.setTitle(title);
            quiz.setDescription(description);
            quiz.setQuizDate(quizDate);
            quiz.setDuration(duration);
            quiz.setMaxMarks(maxMarks);
            quiz.setQuizType(QuizType.QUIZ);
            quiz.setClassEntity(classEntity);
            quiz.setSubject(subject);
            quiz.setTeacher(teacher);

            Quiz savedQuiz = quizRepository.save(quiz);

            if (questions != null && !questions.isEmpty()) {
                for (Map<String, Object> q : questions) {
                    QuizQuestion quizQuestion = new QuizQuestion();
                    quizQuestion.setQuiz(savedQuiz);
                    quizQuestion.setQuestion((String) q.get("question"));
                    quizQuestion.setOptionA((String) q.get("optionA"));
                    quizQuestion.setOptionB((String) q.get("optionB"));
                    quizQuestion.setOptionC((String) q.get("optionC"));
                    quizQuestion.setOptionD((String) q.get("optionD"));

                    // ✅ YAHAN FIX HAI: Correct Answer Save Karo (Case-Insensitive)
                    String correctAnswer = (String) q.get("correctAnswer");
                    if (correctAnswer != null) {
                        quizQuestion.setCorrectAnswer(correctAnswer.toUpperCase()); // 'a', 'b', 'c', 'd' ko 'A', 'B', 'C', 'D' banao
                    }

                    quizQuestion.setMarks(q.get("marks") != null ? Integer.parseInt(q.get("marks").toString()) : 1);
                    quizQuestionRepository.save(quizQuestion);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("id", savedQuiz.getId());
            response.put("title", savedQuiz.getTitle());
            response.put("message", "Quiz with questions created successfully");
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // ✅ SUBMISSIONS
    // ============================================================

    @GetMapping("/submissions")
    public ResponseEntity<?> getAllSubmissions() {
        try {
            List<Submission> submissions = submissionRepository.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Submission s : submissions) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", s.getId());
                map.put("status", s.getStatus());
                map.put("marksObtained", s.getMarksObtained());
                map.put("submissionDate", s.getSubmissionDate());
                map.put("fileUrl", s.getFileUrl());
                map.put("comments", s.getComments());
                map.put("feedback", s.getFeedback());

                if (s.getStudent() != null) {
                    Map<String, Object> studentMap = new HashMap<>();
                    studentMap.put("id", s.getStudent().getId());
                    studentMap.put("studentId", s.getStudent().getStudentId());
                    if (s.getStudent().getUser() != null) {
                        studentMap.put("fullName", s.getStudent().getUser().getFullName());
                        studentMap.put("email", s.getStudent().getUser().getEmail());
                    }
                    map.put("student", studentMap);
                }

                if (s.getAssignment() != null) {
                    Map<String, Object> assignmentMap = new HashMap<>();
                    assignmentMap.put("id", s.getAssignment().getId());
                    assignmentMap.put("title", s.getAssignment().getTitle());
                    assignmentMap.put("maxMarks", s.getAssignment().getMaxMarks());
                    if (s.getAssignment().getSubject() != null) {
                        assignmentMap.put("subjectName", s.getAssignment().getSubject().getSubjectName());
                    }
                    map.put("assignment", assignmentMap);
                }
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/submissions/assignment/{assignmentId}")
    public ResponseEntity<?> getSubmissionsByAssignment(@PathVariable Long assignmentId) {
        try {
            List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (Submission s : submissions) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", s.getId());
                map.put("status", s.getStatus());
                map.put("marksObtained", s.getMarksObtained());
                map.put("submissionDate", s.getSubmissionDate());
                map.put("fileUrl", s.getFileUrl());
                map.put("comments", s.getComments());
                map.put("feedback", s.getFeedback());
                if (s.getStudent() != null && s.getStudent().getUser() != null) {
                    map.put("studentName", s.getStudent().getUser().getFullName());
                    map.put("studentId", s.getStudent().getStudentId());
                }
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<?> gradeSubmission(@PathVariable Long submissionId, @RequestBody Map<String, Object> request) {
        try {
            Integer marksObtained = request.get("marksObtained") != null ?
                    Integer.parseInt(request.get("marksObtained").toString()) : null;
            String feedback = (String) request.get("feedback");

            if (marksObtained == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Marks are required"));
            }

            Submission submission = submissionRepository.findById(submissionId)
                    .orElseThrow(() -> new RuntimeException("Submission not found"));

            submission.setMarksObtained(marksObtained);
            submission.setFeedback(feedback);
            submission.setStatus(SubmissionStatus.GRADED);

            Submission saved = submissionRepository.save(submission);

            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("message", "Submission graded successfully");
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/submissions/student/{studentId}")
    public ResponseEntity<?> getSubmissionsByStudent(@PathVariable Long studentId) {
        try {
            List<Submission> submissions = submissionRepository.findByStudentId(studentId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (Submission s : submissions) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", s.getId());
                map.put("status", s.getStatus());
                map.put("marksObtained", s.getMarksObtained());
                map.put("submissionDate", s.getSubmissionDate());
                map.put("fileUrl", s.getFileUrl());
                map.put("comments", s.getComments());
                map.put("feedback", s.getFeedback());
                if (s.getAssignment() != null) {
                    map.put("assignmentTitle", s.getAssignment().getTitle());
                    map.put("assignmentId", s.getAssignment().getId());
                }
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // ✅ ATTENDANCE
    // ============================================================

    @GetMapping("/attendance/class/{classId}/subject/{subjectId}")
    public ResponseEntity<?> getStudentsForAttendance(@PathVariable Long classId, @PathVariable Long subjectId) {
        try {
            LocalDate today = LocalDate.now();
            List<Student> students = studentRepository.findByClassEntityId(classId);
            List<Attendance> todayAttendance = attendanceRepository.findTodayAttendance(classId, subjectId, today);

            List<Map<String, Object>> result = new ArrayList<>();
            for (Student s : students) {
                Map<String, Object> map = new HashMap<>();
                map.put("studentId", s.getId());
                map.put("studentName", s.getUser().getFullName());
                map.put("rollNumber", s.getRollNumber());

                Optional<Attendance> existing = todayAttendance.stream()
                        .filter(a -> a.getStudent().getId().equals(s.getId()))
                        .findFirst();

                if (existing.isPresent()) {
                    map.put("status", existing.get().getStatus().toString());
                    map.put("attendanceId", existing.get().getId());
                    map.put("marked", true);
                } else {
                    map.put("status", "NOT_MARKED");
                    map.put("attendanceId", null);
                    map.put("marked", false);
                }
                result.add(map);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("date", today.toString());
            response.put("students", result);
            response.put("classId", classId);
            response.put("subjectId", subjectId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/attendance/mark")
    public ResponseEntity<?> markAttendance(@RequestBody Map<String, Object> request) {
        try {
            Long classId = request.get("classId") != null ? Long.parseLong(request.get("classId").toString()) : null;
            Long subjectId = request.get("subjectId") != null ? Long.parseLong(request.get("subjectId").toString()) : null;
            Long teacherId = request.get("teacherId") != null ? Long.parseLong(request.get("teacherId").toString()) : null;
            String dateStr = (String) request.get("date");
            List<Map<String, Object>> attendanceData = (List<Map<String, Object>>) request.get("attendanceData");

            if (classId == null || subjectId == null || teacherId == null || attendanceData == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
            }

            LocalDate date = dateStr != null ? LocalDate.parse(dateStr) : LocalDate.now();

            ClassEntity classEntity = classEntityRepository.findById(classId).orElse(null);
            Subject subject = subjectRepository.findById(subjectId).orElse(null);
            Teacher teacher = teacherRepository.findById(teacherId).orElse(null);

            if (classEntity == null || subject == null || teacher == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid class, subject or teacher"));
            }

            List<Attendance> savedAttendances = new ArrayList<>();

            for (Map<String, Object> data : attendanceData) {
                Long studentId = Long.parseLong(data.get("studentId").toString());
                String statusStr = (String) data.get("status");
                String remarks = (String) data.get("remarks");

                Student student = studentRepository.findById(studentId).orElse(null);
                if (student == null) continue;

                AttendanceStatus status = AttendanceStatus.valueOf(statusStr.toUpperCase());

                Optional<Attendance> existing = attendanceRepository
                        .findByStudentIdAndSubjectIdAndAttendanceDate(studentId, subjectId, date);

                Attendance attendance;
                if (existing.isPresent()) {
                    attendance = existing.get();
                    attendance.setStatus(status);
                    attendance.setRemarks(remarks);
                    attendance.setMarkedBy(teacher);
                } else {
                    attendance = new Attendance();
                    attendance.setStudent(student);
                    attendance.setClassEntity(classEntity);
                    attendance.setSubject(subject);
                    attendance.setAttendanceDate(date);
                    attendance.setStatus(status);
                    attendance.setRemarks(remarks);
                    attendance.setMarkedBy(teacher);
                }
                savedAttendances.add(attendanceRepository.save(attendance));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Attendance marked successfully!");
            response.put("count", savedAttendances.size());
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/attendance/report/{classId}/{subjectId}")
    public ResponseEntity<?> getAttendanceReport(@PathVariable Long classId, @PathVariable Long subjectId) {
        try {
            List<Student> students = studentRepository.findByClassEntityId(classId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (Student s : students) {
                Map<String, Object> map = new HashMap<>();
                map.put("studentId", s.getId());
                map.put("studentName", s.getUser().getFullName());
                map.put("rollNumber", s.getRollNumber());

                Long present = attendanceRepository.countPresentByStudentAndSubject(s.getId(), subjectId);
                Long total = attendanceRepository.countTotalByStudentAndSubject(s.getId(), subjectId);

                map.put("present", present != null ? present : 0);
                map.put("total", total != null ? total : 0);
                map.put("percentage", total != null && total > 0 ?
                        Math.round((present * 100.0 / total) * 100.0) / 100.0 : 0);
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/attendance/teacher/{teacherId}")
    public ResponseEntity<?> getTeacherAttendanceInfo(@PathVariable Long teacherId) {
        try {
            Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
            if (teacher == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Teacher not found"));
            }

            List<Subject> subjects = subjectRepository.findSubjectsByTeacherId(teacherId);
            List<ClassEntity> classes = classEntityRepository.findByClassTeacherId(teacherId);

            Map<String, Object> response = new HashMap<>();
            response.put("teacherId", teacherId);
            response.put("teacherName", teacher.getUser().getFullName());
            response.put("subjects", subjects != null ? subjects : new ArrayList<>());
            response.put("classes", classes != null ? classes : new ArrayList<>());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
// ✅ NOTES ENDPOINTS - Add/Update in TeacherController
// ============================================================

    @GetMapping("/notes")
    public ResponseEntity<?> getTeacherNotes(@RequestParam(required = false) Long teacherId) {
        try {
            System.out.println("📝 Getting notes for teacher ID: " + teacherId);

            if (teacherId == null) {
                // Try to get from session or use default
                return ResponseEntity.badRequest().body(Map.of("error", "Teacher ID is required"));
            }

            List<Map<String, Object>> notes = noteService.getNotesForTeacher(teacherId);
            System.out.println("✅ Found " + notes.size() + " notes");

            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/notes/upload")
    public ResponseEntity<?> uploadNote(
            @RequestParam("file") MultipartFile file,
            @RequestParam("teacherId") Long teacherId,
            @RequestParam("classId") Long classId,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "price", defaultValue = "0") Double price,
            @RequestParam(value = "isFree", defaultValue = "true") Boolean isFree
    ) {
        try {
            System.out.println("📤 Uploading note:");
            System.out.println("  Title: " + title);
            System.out.println("  Teacher ID: " + teacherId);
            System.out.println("  Class ID: " + classId);
            System.out.println("  Subject ID: " + subjectId);
            System.out.println("  Price: " + price);
            System.out.println("  Is Free: " + isFree);
            System.out.println("  File: " + file.getOriginalFilename());
            System.out.println("  File Size: " + file.getSize());
            System.out.println("  Content Type: " + file.getContentType());

            // Validate file
            if (file.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "File is required");
                error.put("success", false);
                return ResponseEntity.badRequest().body(error);
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.equals("application/pdf")) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Only PDF files are allowed. Received: " + contentType);
                error.put("success", false);
                return ResponseEntity.badRequest().body(error);
            }

            if (file.getSize() > 10 * 1024 * 1024) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "File size must be less than 10MB. Current: " + (file.getSize() / 1024 / 1024) + "MB");
                error.put("success", false);
                return ResponseEntity.badRequest().body(error);
            }

            Note note = noteService.uploadNote(
                    file, teacherId, classId, subjectId,
                    title, description, price, isFree
            );

            Map<String, Object> response = new HashMap<>();
            response.put("id", note.getId());
            response.put("message", "Note uploaded successfully!");
            response.put("success", true);
            response.put("fileUrl", note.getFileUrl());
            response.put("fileName", note.getFileName());

            System.out.println("✅ Note uploaded with ID: " + note.getId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/notes/download/{fileName}")
    public ResponseEntity<?> downloadNote(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("uploads/notes/" + fileName);
            System.out.println("📥 Downloading file: " + filePath.toString());

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileBytes = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);

            return ResponseEntity.ok()
                    .header("Content-Type", contentType != null ? contentType : "application/pdf")
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .body(fileBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}