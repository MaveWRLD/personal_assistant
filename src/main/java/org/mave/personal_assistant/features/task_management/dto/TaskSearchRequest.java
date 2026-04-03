package org.mave.personal_assistant.features.task_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mave.personal_assistant.core.domain.enums.TaskPriority;
import org.mave.personal_assistant.core.domain.enums.TaskStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskSearchRequest {
    private String keyword;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime dueBefore;
    private int page = 0;
    private int size = 20;
}

