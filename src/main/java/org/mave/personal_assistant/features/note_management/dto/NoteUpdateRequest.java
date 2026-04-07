package org.mave.personal_assistant.features.note_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for note update requests using Lombok and MapStruct.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteUpdateRequest {
    
    private String title;
    private String content;
}
