package org.mave.personal_assistant.features.note_management.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mave.personal_assistant.core.models.Note;
import org.mave.personal_assistant.features.note_management.dto.NoteCreateRequest;
import org.mave.personal_assistant.features.note_management.dto.NoteResponse;
import org.mave.personal_assistant.features.note_management.dto.NoteUpdateRequest;

/**
 * MapStruct mapper for Note entities and DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NoteMapper {

    /**
     * Creates a new Note from NoteCreateRequest.
     *
     * @param createRequest the DTO with note creation values
     * @return new Note entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Note toEntity(NoteCreateRequest createRequest);

    /**
     * Updates an existing Note entity with values from NoteUpdateRequest.
     * Null values in request are ignored and don't overwrite existing values.
     *
     * @param updateRequest the DTO with update values
     * @param existingNote the existing note entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateNoteFromDto(NoteUpdateRequest updateRequest, @MappingTarget Note existingNote);

    /**
     * Maps a Note entity to NoteResponse.
     *
     * @param note the Note entity
     * @return NoteResponse DTO
     */
    NoteResponse toResponse(Note note);
}
