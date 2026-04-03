package org.mave.personal_assistant.features.task_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mave.personal_assistant.core.domain.enums.TaskPriority;
import org.mave.personal_assistant.core.domain.enums.TaskStatus;

import java.time.LocalDateTime;

/**
 * DTO for task update requests using Lombok and MapStruct.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskUpdateRequest {
    
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime dueDate;
}
