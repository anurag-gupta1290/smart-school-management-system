package com.smart_school_management_system.smart_school_2026.repository;

import com.smart_school_management_system.smart_school_2026.entity.NoteRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRatingRepository extends JpaRepository<NoteRating, Long> {

    Optional<NoteRating> findByNoteIdAndStudentId(Long noteId, Long studentId);

    List<NoteRating> findByNoteId(Long noteId);

    @Query("SELECT AVG(nr.rating) FROM NoteRating nr WHERE nr.note.id = :noteId")
    Double findAverageRatingByNoteId(@Param("noteId") Long noteId);
}