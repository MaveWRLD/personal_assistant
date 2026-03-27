package org.mave.personal_assistant.service;

import org.mave.personal_assistant.dto.requests.TaskUpdateRequest;
import org.mave.personal_assistant.models.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDateTime;

/**
 * MapStruct mapper for Task entities and DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {
    
    /**
     * Updates an existing Task entity with values from TaskUpdateRequest.
     * Null values in the request are ignored and don't overwrite existing values.
     * 
     * @param updateRequest the DTO with update values
     * @param existingTask the existing task entity to update
     */
    void updateTaskFromDto(TaskUpdateRequest updateRequest, @MappingTarget Task existingTask);
    
    /**
     * Creates a new Task from TaskUpdateRequest.
     * 
     * @param updateRequest the DTO with task values
     * @return new Task entity
     */
    Task createTaskFromDto(TaskUpdateRequest updateRequest);
    
    /**
     * Helper method to set updated timestamp.
     * 
     * @return current LocalDateTime
     */
    default LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}
