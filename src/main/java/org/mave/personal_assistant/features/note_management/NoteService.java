package org.mave.personal_assistant.features.note_management;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mave.personal_assistant.core.models.Note;
import org.mave.personal_assistant.features.note_management.dto.NoteCreateRequest;
import org.mave.personal_assistant.features.note_management.dto.NoteResponse;
import org.mave.personal_assistant.features.note_management.dto.NoteUpdateRequest;
import org.mave.personal_assistant.features.note_management.mapper.NoteMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing notes with database persistence.
 * Provides CRUD operations and business logic for note management.
 */
@Slf4j
@Service
@AllArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    /**
     * Creates a new note.
     *
     * @param createRequest DTO with note creation details
     * @return The created note response
     * @throws IllegalArgumentException if title is null or empty
     */
    public NoteResponse createNote(NoteCreateRequest createRequest) {
        log.info("Creating new note with title: {}", createRequest.getTitle());
        validateNoteTitle(createRequest.getTitle());

        Note newNote = noteMapper.toEntity(createRequest);
        Note savedNote = noteRepository.save(newNote);
        log.info("Note created successfully with ID: {}, title: {}", savedNote.getId(), savedNote.getTitle());
        return noteMapper.toResponse(savedNote);
    }

    /**
     * Retrieves all notes ordered by creation date (newest first).
     *
     * @return List of all note responses
     */
    public List<NoteResponse> getAllNotes() {
        log.info("Retrieving all notes");
        return noteRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(noteMapper::toResponse)
                .toList();
    }

    /**
     * Retrieves a note by its ID.
     *
     * @param id Note ID
     * @return Optional containing the note response if found, empty otherwise
     */
    public Optional<NoteResponse> getNoteById(Long id) {
        log.debug("Retrieving note with ID: {}", id);
        return noteRepository.findById(id)
                .map(noteMapper::toResponse);
    }

    /**
     * Updates an existing note.
     *
     * @param id Note ID
     * @param updateRequest DTO with update values
     * @return Optional containing the updated note response if found, empty otherwise
     * @throws IllegalArgumentException if new title is null or empty
     */
    public Optional<NoteResponse> updateNote(Long id, NoteUpdateRequest updateRequest) {
        log.info("Updating note with ID: {}", id);
        Optional<Note> existingNoteOpt = noteRepository.findById(id);
        if (existingNoteOpt.isEmpty()) {
            log.warn("Note not found for update with ID: {}", id);
            return Optional.empty();
        }

        Note existingNote = existingNoteOpt.get();
        
        if (updateRequest.getTitle() != null) {
            validateNoteTitle(updateRequest.getTitle());
        }
        
        noteMapper.updateNoteFromDto(updateRequest, existingNote);
        existingNote.setUpdatedAt(Instant.now());

        Note updatedNote = noteRepository.save(existingNote);
        log.info("Note updated successfully with ID: {}, title: {}", updatedNote.getId(), updatedNote.getTitle());
        return Optional.of(noteMapper.toResponse(updatedNote));
    }

    /**
     * Deletes a note by its ID.
     *
     * @param id Note ID
     * @return true if note was deleted, false if not found
     */
    public boolean deleteNote(Long id) {
        log.info("Attempting to delete note with ID: {}", id);
        if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id);
            log.info("Note deleted successfully with ID: {}", id);
            return true;
        } else {
            log.warn("Failed to delete note - not found with ID: {}", id);
            return false;
        }
    }

    /**
     * Gets the total number of notes.
     *
     * @return Total note count
     */
    public long getNoteCount() {
        return noteRepository.count();
    }

    private void validateNoteTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Note title cannot be null or empty");
        }
    }
}
