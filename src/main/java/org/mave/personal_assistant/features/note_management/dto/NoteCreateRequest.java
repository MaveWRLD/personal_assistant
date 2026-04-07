package org.mave.personal_assistant.features.note_management.dto;

import dev.langchain4j.model.output.structured.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for note creation requests using Lombok and MapStruct.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteCreateRequest {
    
    @Description("The title of the note. Must not be null or empty. Provides a brief description of what the note is about.")
    private String title;
    
    @Description("The content of the note. Provides the main content or body of the note. " +
            "Can be null or empty if no content is needed.")
    private String content;
}
