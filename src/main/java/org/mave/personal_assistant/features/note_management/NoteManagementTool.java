package org.mave.personal_assistant.features.note_management;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mave.personal_assistant.features.note_management.dto.NoteCreateRequest;
import org.mave.personal_assistant.features.note_management.dto.NoteResponse;
import org.mave.personal_assistant.features.note_management.dto.NoteUpdateRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class NoteManagementTool {

    private final NoteService noteService;

    @Tool("Get all notes ordered by newest first")
    public List<NoteResponse> getAllNotes() {
        log.debug("Getting all notes");
        List<NoteResponse> notes = noteService.getAllNotes();
        return notes != null ? notes : List.of();
    }

    @Tool("""
        Get a specific note by its ID.
        If the note exists, returns the note details as JSON object.
        If the note does not exist, returns null.
        """)
    public Optional<NoteResponse> getNoteById(
            @P("The ID of the note to retrieve. It should be a positive Long number.") Long id) {
        
        log.debug("Getting note with ID: {}", id);
        return noteService.getNoteById(id);
    }

    @Tool("""
        Create a new note.
        Required: title.
        Optional: content.
        """)
    public NoteResponse createNote(
            @P("The title of the note - REQUIRED") String title,
            @P("The content/body of the note - optional") String content) {
        
        log.info("Tool createNote called → title={}, content={}", title, content);
        
        try {
            NoteCreateRequest request = NoteCreateRequest.builder()
                    .title(title)
                    .content(content)
                    .build();
            return noteService.createNote(request);
        } catch (Exception e) {
            log.error("Failed to create note", e);
            throw new RuntimeException("Failed to create note: " + e.getMessage(), e);
        }
    }

    @Tool("""
        Update an existing note.
        Required: id.
        Optional: title, content.
        """)
    public Optional<NoteResponse> updateNote(
            @P("The ID of the note to update - REQUIRED") Long id,
            @P("The new title for the note - optional") String title,
            @P("The new content for the note - optional") String content) {
        
        log.info("Tool updateNote called → id={}, title={}, content={}", id, title, content);
    
        NoteUpdateRequest request = NoteUpdateRequest.builder()
                .title(title)
                .content(content)
                .build();
        return noteService.updateNote(id, request);
    
    }

    @Tool("Delete a note by ID")
    public boolean deleteNote(@P("The ID of the note to delete") Long id) {
        log.info("Tool deleteNote called → id={}", id);

        return noteService.deleteNote(id);
    }

    @Tool("Get the total count of notes")
    public String getNoteCount() {
        log.debug("Getting note count");
        long count = noteService.getNoteCount();
        return "Total notes: " + count;
    }
}
