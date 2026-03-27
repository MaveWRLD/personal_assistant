package org.mave.personal_assistant.dto.requests;

import org.mave.personal_assistant.models.enums.TaskPriority;
import org.mave.personal_assistant.models.enums.TaskStatus;

import java.time.LocalDateTime;

public class TaskCreateRequest {
    String title;
    String description;
    TaskPriority priority;
    TaskStatus status;
    LocalDateTime dueDate;
}
