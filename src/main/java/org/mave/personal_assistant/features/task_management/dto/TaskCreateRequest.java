package org.mave.personal_assistant.features.task_management.dto;

import dev.langchain4j.model.output.structured.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mave.personal_assistant.core.domain.enums.TaskPriority;
import org.mave.personal_assistant.core.domain.enums.TaskStatus;

import java.time.LocalDateTime;

/**
 * DTO for task creation requests using Lombok and MapStruct.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreateRequest {
    
    @Description("The title of the task. Must not be null or empty. Provides a brief description of what the task is about.")
    private String title;
    
    @Description("Detailed description of the task. Provides additional context, requirements, or steps needed to complete the task. " +
            "Can be null if no additional description is needed.")
    private String description;
    
    @Description("The priority level of the task. Determines the importance and urgency of the task." +
            " Must not be null.")
    private TaskPriority priority;
    
    @Description("The current status of the task. If not provided, defaults to TODO status." +
            " Can be null to use default status.")
    private TaskStatus status;
    
    @Description("The deadline date and time for task completion. Specifies when the task should be completed." +
            " Can be null if no deadline is set.")
    private LocalDateTime dueDate;
}
