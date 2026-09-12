package com.smart_school_management_system.smart_school_2026.controller;

import com.smart_school_management_system.smart_school_2026.entity.*;
import com.smart_school_management_system.smart_school_2026.repository.*;
import com.smart_school_management_system.smart_school_2026.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@Transactional
public class StudentController {

    private final StudentService studentService;
    private final AssignmentRepository assignmentRepository;
    private final QuizRepository quizRepository;
    private final SubmissionRepository submissionRepository;
    private final StudentRepository studentRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final AttendanceRepository attendanceRepository;
    private final QuizResultRepository quizResultRepository;
    private final ClassEntityRepository classEntityRepository;
    private final NoteRepository noteRepository;

    // ============================================================
    // DASHBOARD & PROFILE
    // ============================================================
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
    // ASSIGNMENTS
    // ============================================================
    @GetMapping("/assignments/{studentId}")
    public ResponseEntity<?> getAssignmentsForStudent(@PathVariable Long studentId) {
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
                map.put("className", a.getClassEntity() != null ? a.getClassEntity().getClassName() : "N/A");
                map.put("subjectName", a.getSubject() != null ? a.getSubject().getSubjectName() : "N/A");
                map.put("teacherName", a.getTeacher() != null && a.getTeacher().getUser() != null ? a.getTeacher().getUser().getFullName() : "N/A");

                // ✅ FIX: Sirf SUBMITTED, GRADED, LATE ko submitted count karo
                Optional<Submission> existingSubmission = submissionRepository
                        .findByAssignmentIdAndStudentId(a.getId(), studentId);

                boolean submitted = existingSubmission.isPresent() &&
                        (existingSubmission.get().getStatus() == SubmissionStatus.SUBMITTED ||
                                existingSubmission.get().getStatus() == SubmissionStatus.GRADED ||
                                existingSubmission.get().getStatus() == SubmissionStatus.LATE);

                map.put("submitted", submitted);
                map.put("submissionStatus", existingSubmission.isPresent() ?
                        existingSubmission.get().getStatus().toString() : "NOT_SUBMITTED");

                // ✅ Extra info
                if (existingSubmission.isPresent()) {
                    Submission s = existingSubmission.get();
                    map.put("marksObtained", s.getMarksObtained());
                    map.put("feedback", s.getFeedback());
                    map.put("submissionDate", s.getSubmissionDate());
                    map.put("fileUrl", s.getFileUrl());
                }

                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @PostMapping("/assignments/{assignmentId}/submit")
    public ResponseEntity<?> submitAssignment(@PathVariable Long assignmentId, @RequestBody Map<String, Object> request) {
        try {
            Long studentId = null;
            if (request.get("studentId") != null) {
                studentId = Long.parseLong(request.get("studentId").toString());
            }

            if (studentId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Student ID is required"));
            }

            Optional<Submission> existing = submissionRepository
                    .findByAssignmentIdAndStudentId(assignmentId, studentId);

            // ✅ FIX: Sirf SUBMITTED, GRADED, LATE ko "Already submitted" bolo
            if (existing.isPresent() &&
                    (existing.get().getStatus() == SubmissionStatus.SUBMITTED ||
                            existing.get().getStatus() == SubmissionStatus.GRADED ||
                            existing.get().getStatus() == SubmissionStatus.LATE)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Already submitted"));
            }

            Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
            Student student = studentRepository.findById(studentId).orElse(null);

            if (assignment == null || student == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Assignment or Student not found"));
            }

            String fileUrl = (String) request.get("fileUrl");
            if (fileUrl != null && fileUrl.contains("localhost")) {
                fileUrl = fileUrl.replace("localhost:8083", "15.207.174.107:8083");
            }
            String comments = (String) request.get("comments");

            Submission submission;
            // ✅ FIX: Agar PENDING submission exist karta hai toh update karo, warna naya banao
            if (existing.isPresent()) {
                submission = existing.get();
                submission.setFileUrl(fileUrl);
                submission.setComments(comments);
                submission.setSubmissionDate(LocalDateTime.now());
                submission.setStatus(SubmissionStatus.SUBMITTED);
            } else {
                submission = new Submission();
                submission.setAssignment(assignment);
                submission.setStudent(student);
                submission.setSubmissionDate(LocalDateTime.now());
                submission.setFileUrl(fileUrl);
                submission.setComments(comments);
                submission.setStatus(SubmissionStatus.SUBMITTED);
            }

            submissionRepository.save(submission);

            List<Map<String, Object>> updatedAssignments = getUpdatedAssignments(studentId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Assignment submitted successfully!");
            response.put("assignments", updatedAssignments);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private List<Map<String, Object>> getUpdatedAssignments(Long studentId) {
        List<Assignment> assignments = assignmentRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Assignment a : assignments) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", a.getId());
            map.put("title", a.getTitle());
            map.put("dueDate", a.getDueDate() != null ? a.getDueDate().toString() : "");
            map.put("maxMarks", a.getMaxMarks());
            map.put("className", a.getClassEntity() != null ? a.getClassEntity().getClassName() : "N/A");
            map.put("subjectName", a.getSubject() != null ? a.getSubject().getSubjectName() : "N/A");

            // ✅ FIX: Proper status check
            Optional<Submission> existing = submissionRepository
                    .findByAssignmentIdAndStudentId(a.getId(), studentId);

            boolean submitted = existing.isPresent() &&
                    (existing.get().getStatus() == SubmissionStatus.SUBMITTED ||
                            existing.get().getStatus() == SubmissionStatus.GRADED ||
                            existing.get().getStatus() == SubmissionStatus.LATE);

            map.put("submitted", submitted);
            result.add(map);
        }
        return result;
    }

    // ============================================================
    // QUIZZES
    // ============================================================
    @GetMapping("/quizzes/{studentId}")
    public ResponseEntity<?> getQuizzesForStudent(@PathVariable Long studentId) {
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
                map.put("className", q.getClassEntity() != null ? q.getClassEntity().getClassName() : "N/A");
                map.put("subjectName", q.getSubject() != null ? q.getSubject().getSubjectName() : "N/A");
                map.put("teacherName", q.getTeacher() != null && q.getTeacher().getUser() != null ? q.getTeacher().getUser().getFullName() : "N/A");

                List<QuizQuestion> questions = quizQuestionRepository.findByQuizId(q.getId());
                List<Map<String, Object>> questionList = new ArrayList<>();
                for (QuizQuestion question : questions) {
                    Map<String, Object> qMap = new HashMap<>();
                    qMap.put("id", question.getId());
                    qMap.put("question", question.getQuestion());
                    qMap.put("optionA", question.getOptionA());
                    qMap.put("optionB", question.getOptionB());
                    qMap.put("optionC", question.getOptionC());
                    qMap.put("optionD", question.getOptionD());
                    qMap.put("correctAnswer", question.getCorrectAnswer());
                    qMap.put("marks", question.getMarks() != null ? question.getMarks() : 1);
                    questionList.add(qMap);
                }
                map.put("questions", questionList);

                // ✅ FIX: Proper status check for quiz
                Optional<QuizResult> existingResult = quizResultRepository
                        .findByQuizIdAndStudentId(q.getId(), studentId);

                boolean attempted = existingResult.isPresent();
                map.put("attempted", attempted);
                map.put("status", attempted ? "COMPLETED" : "UPCOMING");

                // ✅ Extra info
                if (attempted) {
                    QuizResult qr = existingResult.get();
                    map.put("marksObtained", qr.getMarksObtained());
                    map.put("totalMarks", qr.getTotalMarks());
                    map.put("percentage", qr.getPercentage());
                    map.put("grade", qr.getGrade());
                    map.put("attemptDate", qr.getAttemptDate());
                }

                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @PostMapping("/quizzes/{quizId}/submit")
    public ResponseEntity<?> submitQuiz(@PathVariable Long quizId, @RequestBody Map<String, Object> request) {
        try {
            Long studentId = request.get("studentId") != null ? Long.parseLong(request.get("studentId").toString()) : null;

            if (studentId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Student ID is required"));
            }

            // ✅ FIX: Pehle check karo ki already attempt kiya hai ya nahi
            Optional<QuizResult> existingResult = quizResultRepository
                    .findByQuizIdAndStudentId(quizId, studentId);

            if (existingResult.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Already attempted this quiz",
                        "marksObtained", existingResult.get().getMarksObtained(),
                        "grade", existingResult.get().getGrade()
                ));
            }

            List<Map<String, Object>> answers = (List<Map<String, Object>>) request.get("answers");
            int marksObtained = 0;

            if (answers != null) {
                for (Map<String, Object> ans : answers) {
                    Long questionId = Long.parseLong(ans.get("questionId").toString());
                    String selectedAnswer = String.valueOf(ans.get("selectedAnswer")).toUpperCase();

                    QuizQuestion question = quizQuestionRepository.findById(questionId).orElse(null);

                    System.out.println("QID: " + questionId + " | Selected: " + selectedAnswer + " | Correct: " + (question != null ? question.getCorrectAnswer() : "NULL"));

                    if (question != null && question.getCorrectAnswer() != null) {
                        if (question.getCorrectAnswer().equalsIgnoreCase(selectedAnswer)) {
                            marksObtained += question.getMarks() != null ? question.getMarks() : 1;
                        }
                    }
                }
            }

            Quiz quiz = quizRepository.findById(quizId).orElse(null);
            Student student = studentRepository.findById(studentId).orElse(null);

            if (quiz == null || student == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Quiz or Student not found"));
            }

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
            response.put("marksObtained", saved.getMarksObtained());
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

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
    // SUBMISSIONS
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

                String fileUrl = s.getFileUrl();
                if (fileUrl != null && fileUrl.contains("localhost")) {
                    fileUrl = fileUrl.replace("localhost:8083", "15.207.174.107:8083");
                }
                map.put("fileUrl", fileUrl);

                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // RESULTS
    // ============================================================
    @GetMapping("/results/{studentId}")
    public ResponseEntity<?> getStudentResults(@PathVariable Long studentId) {
        try {
            List<Submission> submissions = submissionRepository.findByStudentId(studentId);

            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> subjectWiseMarks = new ArrayList<>();
            int totalMarks = 0;
            int totalMaxMarks = 0;
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
                    totalMaxMarks += s.getAssignment() != null ? s.getAssignment().getMaxMarks() : 100;
                    gradedCount++;
                }
            }

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

    private String calculateGrade(double percentage) {
        if (percentage >= 90) return "A+";
        else if (percentage >= 80) return "A";
        else if (percentage >= 70) return "B";
        else if (percentage >= 60) return "C";
        else if (percentage >= 50) return "D";
        else if (percentage >= 40) return "E";
        else return "F";
    }

    // ============================================================
    // ATTENDANCE
    // ============================================================
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

    // ============================================================
    // NOTES
    // ============================================================
    @GetMapping("/notes/{studentId}")
    public ResponseEntity<?> getNotesForStudent(@PathVariable Long studentId) {
        try {
            List<Note> notes = noteRepository.findAccessibleNotesForStudent(studentId);
            List<Map<String, Object>> result = new ArrayList<>();

            for (Note n : notes) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", n.getId());
                map.put("title", n.getTitle());
                map.put("description", n.getDescription());

                if (n.getSubject() != null) {
                    map.put("subjectName", n.getSubject().getSubjectName());
                } else {
                    map.put("subjectName", "N/A");
                }

                if (n.getClassEntity() != null) {
                    map.put("className", n.getClassEntity().getClassName());
                } else {
                    map.put("className", "N/A");
                }

                map.put("price", n.getPrice() != null ? n.getPrice() : 0);
                map.put("isFree", n.getIsFree() != null ? n.getIsFree() : (n.getPrice() == null || n.getPrice() == 0));

                map.put("uploadDate", n.getCreatedAt() != null ? n.getCreatedAt().toString() : "");

                if (n.getTeacher() != null && n.getTeacher().getUser() != null) {
                    map.put("teacherName", n.getTeacher().getUser().getFullName());
                } else {
                    map.put("teacherName", "N/A");
                }

                boolean isPurchased = noteRepository.findPurchasedNotesByStudent(studentId)
                        .stream().anyMatch(p -> p.getId().equals(n.getId()));
                map.put("purchased", isPurchased);

                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/notes/download/{noteId}")
    public ResponseEntity<?> downloadNote(@PathVariable Long noteId) {
        try {
            Note note = noteRepository.findById(noteId).orElse(null);
            if (note == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Note not found"));
            }

            String fileUrlStr = note.getFileUrl();
            if (fileUrlStr == null || fileUrlStr.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File URL not found for this note"));
            }

            String fileName = fileUrlStr.substring(fileUrlStr.lastIndexOf('/') + 1);

            Path filePath = Paths.get("uploads", "notes", fileName).normalize();

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File not found on server. Checked path: " + filePath.toString()));
            }

            String originalFileName = note.getFileName();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFileName + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error downloading file: " + e.getMessage()));
        }
    }
}