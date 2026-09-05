package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByTeacherId(Long teacherId);

    List<Note> findByClassEntityId(Long classId);

    List<Note> findBySubjectId(Long subjectId);

    @Query("SELECT n FROM Note n WHERE n.isFree = true OR n.id IN " +
            "(SELECT np.note.id FROM NotePurchase np WHERE np.student.id = :studentId)")
    List<Note> findAccessibleNotesForStudent(@Param("studentId") Long studentId);

    @Query("SELECT n FROM Note n WHERE n.id IN " +
            "(SELECT np.note.id FROM NotePurchase np WHERE np.student.id = :studentId)")
    List<Note> findPurchasedNotesByStudent(@Param("studentId") Long studentId);
}