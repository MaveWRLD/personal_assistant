package org.mave.personal_assistant.features.task_management.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mave.personal_assistant.core.models.Task;
import org.mave.personal_assistant.features.task_management.dto.TaskCreateRequest;
import org.mave.personal_assistant.features.task_management.dto.TaskResponse;
import org.mave.personal_assistant.features.task_management.dto.TaskUpdateRequest;

/**
 * MapStruct mapper for Task entities and DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {

    /**
     * Creates a new Task from TaskCreateRequest.
     *
     * @param createRequest the DTO with task creation values
     * @return new Task entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Task toEntity(TaskCreateRequest createRequest);

    /**
     * Updates an existing Task entity with values from TaskUpdateRequest.
     * Null values in request are ignored and don't overwrite existing values.
     *
     * @param updateRequest the DTO with update values
     * @param existingTask the existing task entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateTaskFromDto(TaskUpdateRequest updateRequest, @MappingTarget Task existingTask);

    /**
     * Maps a Task entity to TaskResponse.
     *
     * @param task the Task entity
     * @return TaskResponse DTO
     */
    TaskResponse toResponse(Task task);
}
