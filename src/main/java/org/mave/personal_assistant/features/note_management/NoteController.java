package org.mave.personal_assistant.features.note_management;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mave.personal_assistant.features.note_management.dto.NoteCreateRequest;
import org.mave.personal_assistant.features.note_management.dto.NoteResponse;
import org.mave.personal_assistant.features.note_management.dto.NoteUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(@RequestBody NoteCreateRequest createRequest) {
        log.info("Creating new note with title: {}", createRequest.getTitle());
        var createdNote = noteService.createNote(createRequest);
        return ResponseEntity.ok(createdNote);
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getAllNotes() {
        log.info("Retrieving all notes");
        var notes = noteService.getAllNotes();
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getNote(@PathVariable Long id) {
        log.info("Retrieving note with ID: {}", id);
        Optional<NoteResponse> noteResponse = noteService.getNoteById(id);
        return noteResponse.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(@PathVariable Long id, @RequestBody NoteUpdateRequest updateRequest) {
        log.info("Updating note with ID: {}", id);
        Optional<NoteResponse> updatedNote = noteService.updateNote(id, updateRequest);
        return updatedNote.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        log.info("Deleting note with ID: {}", id);
        boolean deleted = noteService.deleteNote(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getNoteCount() {
        log.info("Retrieving note count");
        long count = noteService.getNoteCount();
        return ResponseEntity.ok(count);
    }
}
