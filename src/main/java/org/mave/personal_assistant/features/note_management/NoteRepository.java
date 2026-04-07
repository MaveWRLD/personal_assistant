package org.mave.personal_assistant.features.note_management;

import java.util.List;

import org.mave.personal_assistant.core.models.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long>, JpaSpecificationExecutor<Note> {
    
    /**
     * Find all notes ordered by creation date descending (newest first).
     */
    List<Note> findAllByOrderByCreatedAtDesc();
}
