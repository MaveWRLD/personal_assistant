package org.mave.personal_assistant.dto.requests;

import lombok.Builder;
import lombok.Data;
import org.mave.personal_assistant.models.Task;
import org.mave.personal_assistant.models.enums.TaskPriority;
import org.mave.personal_assistant.models.enums.TaskStatus;

import java.time.LocalDateTime;

/**
 * DTO for task update requests using Lombok and MapStruct.
 */
@Data
@Builder
public class TaskUpdateRequest {
    
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private String assignedTo;
    private LocalDateTime dueDate;
}
