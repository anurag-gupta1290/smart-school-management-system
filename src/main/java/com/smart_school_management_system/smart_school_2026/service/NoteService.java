package com.smart_school_management_system.smart_school_2026.service;

import com.smart_school_management_system.smart_school_2026.entity.*;
import com.smart_school_management_system.smart_school_2026.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;
    private final NotePurchaseRepository notePurchaseRepository;
    private final NoteRatingRepository noteRatingRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final ClassEntityRepository classEntityRepository;
    private final StudentRepository studentRepository;

    private static final String UPLOAD_DIR = "uploads/notes/";

    public Note uploadNote(MultipartFile file, Long teacherId, Long classId,
                           Long subjectId, String title, String description,
                           Double price, Boolean isFree) throws IOException {

        // Create upload directory if not exists
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(fileName);

        // Save file
        Files.copy(file.getInputStream(), filePath);

        // Get entities
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + teacherId));
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with ID: " + classId));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + subjectId));

        // Create note entity
        Note note = new Note();
        note.setTeacher(teacher);
        note.setClassEntity(classEntity);
        note.setSubject(subject);
        note.setTitle(title);
        note.setDescription(description);
        note.setFileUrl("/api/teacher/notes/download/" + fileName);
        note.setFileName(originalFilename);
        note.setFileSize(file.getSize());
        note.setPrice(price != null ? price : 0.0);
        note.setIsFree(isFree != null ? isFree : (price == null || price == 0));
        note.setDownloads(0);
        note.setRating(0.0);

        return noteRepository.save(note);
    }

    public List<Map<String, Object>> getNotesForTeacher(Long teacherId) {
        List<Note> notes = noteRepository.findByTeacherId(teacherId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Note n : notes) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", n.getId());
            map.put("title", n.getTitle());
            map.put("description", n.getDescription());
            map.put("fileUrl", n.getFileUrl());
            map.put("fileName", n.getFileName());
            map.put("fileSize", n.getFileSize());
            map.put("price", n.getPrice());
            map.put("isFree", n.getIsFree());
            map.put("downloads", n.getDownloads());
            map.put("rating", n.getRating());
            map.put("createdAt", n.getCreatedAt());

            // Safely get subject name
            if (n.getSubject() != null) {
                map.put("subjectName", n.getSubject().getSubjectName());
            } else {
                map.put("subjectName", "N/A");
            }

            // Safely get class name
            if (n.getClassEntity() != null) {
                map.put("className", n.getClassEntity().getClassName());
                map.put("section", n.getClassEntity().getSection());
            } else {
                map.put("className", "N/A");
                map.put("section", "");
            }

            result.add(map);
        }
        return result;
    }

    public List<Map<String, Object>> getAccessibleNotesForStudent(Long studentId) {
        List<Note> notes = noteRepository.findAccessibleNotesForStudent(studentId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Note n : notes) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", n.getId());
            map.put("title", n.getTitle());
            map.put("description", n.getDescription());
            map.put("fileUrl", n.getFileUrl());
            map.put("fileName", n.getFileName());
            map.put("fileSize", n.getFileSize());
            map.put("price", n.getPrice());
            map.put("isFree", n.getIsFree());
            map.put("downloads", n.getDownloads());
            map.put("rating", n.getRating());
            map.put("createdAt", n.getCreatedAt());

            if (n.getSubject() != null) {
                map.put("subjectName", n.getSubject().getSubjectName());
            } else {
                map.put("subjectName", "N/A");
            }

            if (n.getClassEntity() != null) {
                map.put("className", n.getClassEntity().getClassName());
                map.put("section", n.getClassEntity().getSection());
            } else {
                map.put("className", "N/A");
                map.put("section", "");
            }

            // Check if purchased
            boolean isPurchased = notePurchaseRepository
                    .existsByNoteIdAndStudentId(n.getId(), studentId);
            map.put("isPurchased", isPurchased);

            result.add(map);
        }
        return result;
    }

    public NotePurchase purchaseNote(Long noteId, Long studentId, Double amount,
                                     String paymentMethod, String transactionId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found with ID: " + noteId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        // Check if already purchased
        if (notePurchaseRepository.existsByNoteIdAndStudentId(noteId, studentId)) {
            throw new RuntimeException("Already purchased this note");
        }

        NotePurchase purchase = new NotePurchase();
        purchase.setNote(note);
        purchase.setStudent(student);
        purchase.setAmountPaid(amount != null ? amount : note.getPrice());
        purchase.setPaymentMethod(paymentMethod);
        purchase.setTransactionId(transactionId);
        purchase.setStatus(PurchaseStatus.COMPLETED);

        // Increment downloads
        note.setDownloads(note.getDownloads() + 1);
        noteRepository.save(note);

        return notePurchaseRepository.save(purchase);
    }

    public boolean hasAccessToNote(Long noteId, Long studentId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        // Free notes are accessible to everyone
        if (note.getIsFree()) {
            return true;
        }

        // Check if purchased
        return notePurchaseRepository.existsByNoteIdAndStudentId(noteId, studentId);
    }

    public void incrementDownloads(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        note.setDownloads(note.getDownloads() + 1);
        noteRepository.save(note);
    }

    public NoteRating rateNote(Long noteId, Long studentId, Integer rating, String review) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Check if user has purchased or note is free
        if (!note.getIsFree() && !notePurchaseRepository.existsByNoteIdAndStudentId(noteId, studentId)) {
            throw new RuntimeException("You need to purchase this note to rate it");
        }

        // Check if already rated
        Optional<NoteRating> existing = noteRatingRepository.findByNoteIdAndStudentId(noteId, studentId);
        if (existing.isPresent()) {
            NoteRating existingRating = existing.get();
            existingRating.setRating(rating);
            existingRating.setReview(review);
            return noteRatingRepository.save(existingRating);
        }

        NoteRating noteRating = new NoteRating();
        noteRating.setNote(note);
        noteRating.setStudent(student);
        noteRating.setRating(rating);
        noteRating.setReview(review);

        NoteRating saved = noteRatingRepository.save(noteRating);

        // Update average rating
        Double avgRating = noteRatingRepository.findAverageRatingByNoteId(noteId);
        note.setRating(avgRating != null ? avgRating : 0.0);
        noteRepository.save(note);

        return saved;
    }

    public Note getNoteById(Long noteId) {
        return noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found with ID: " + noteId));
    }
}