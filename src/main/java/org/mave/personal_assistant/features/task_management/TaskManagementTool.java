package org.mave.personal_assistant.features.task_management;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mave.personal_assistant.features.task_management.dto.TaskCreateRequest;
import org.mave.personal_assistant.features.task_management.dto.TaskResponse;
import org.mave.personal_assistant.features.task_management.dto.TaskSearchRequest;
import org.mave.personal_assistant.features.task_management.dto.TaskUpdateRequest;
import org.mave.personal_assistant.core.models.enums.TaskPriority;
import org.mave.personal_assistant.core.models.enums.TaskStatus;
import org.mave.personal_assistant.shared.exception.TaskNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class TaskManagementTool {

    private final TaskService taskService;

    @Tool("Get current date and time")
    public String getCurrentTime() {
        log.debug("Getting current time");
        String currentTime = LocalDateTime.now().toString();
        log.debug("Current time retrieved: {}", currentTime);
        return currentTime;
    }

    @Tool("""
        Create a new task.
        Required: title, dueDate (use parseDate tool first if needed), priority.
        Optional: description, status.
    """)
    public TaskResponse addTask(
            @P("The clear title of task") String title,
            @P("Due date -  REQUIRED") LocalDateTime dueDate,
            @P("Priority: HIGH, MEDIUM, or LOW") String priority,
            @P("Optional additional details") String description,
            @P("Optional status: TODO, IN_PROGRESS, DONE") String status) {

        log.info("Tool addTask called → title={}, dueDate={}, priority={}, description={}",
                title, dueDate, priority, description);

        try {
            TaskCreateRequest request = TaskCreateRequest.builder()
                    .title(title)
                    .dueDate(dueDate)
                    .priority(TaskPriority.valueOf(priority != null ? priority.toUpperCase() : "MEDIUM"))
                    .description(description)
                    .status(status != null && !status.trim().isEmpty() ? TaskStatus.valueOf(status.toUpperCase()) : null)
                    .build();
            
            return taskService.createTask(request);
        } catch (Exception e) {
            log.error("Failed to create task", e);
            throw new RuntimeException("Failed to create task: " + e.getMessage(), e);
        }
    }

    @Tool("Get all tasks")
    public List<TaskResponse> getAllTasks() {
        log.debug("Getting all tasks");
        List<TaskResponse> tasks = taskService.getAllTasks();
        return tasks != null ? tasks : List.of();
    }

    @Tool("""
        Get a specific task by its ID.
        If the task exists, returns the task details as JSON object.
        If the task does not exist, returns null.
        """)
    public Optional<TaskResponse> getTaskById(
            @P("The ID of the task to retrieve. It should be a positive Long number.") Long id) {
        
        log.debug("Getting task with ID: {}", id);
        try {
            return Optional.ofNullable(taskService.getTaskById(id));
        } catch (TaskNotFoundException e) {
            return Optional.empty();
        }
    }

    @Tool("""
    Search and filter tasks.
    
    All parameters are optional. Provide any combination of:
    - keyword: Search term to match against task title or description
    - status: Filter by status - TODO, IN_PROGRESS, or DONE
    - priority: Filter by priority - HIGH, MEDIUM, or LOW
    - dueBefore: Return only tasks due before this date (LocalDateTime format)
    - page: Page number for pagination (default: 0)
    - size: Number of tasks per page (default: 20)
    """)
    public TaskSearchResult searchTasks(
            @P("Keyword to search in title or description (optional)") String keyword,
            @P("Filter by status: TODO, IN_PROGRESS, or DONE (optional)") String status,
            @P("Filter by priority: HIGH, MEDIUM, or LOW (optional)") String priority,
            @P("Return tasks due before this date (optional)") LocalDateTime dueBefore,
            @P("Page number, starting from 0 (optional)") Integer page,
            @P("Number of results per page (optional)") Integer size) {

        log.info("Tool searchTasks called with keyword: {}, status: {}, priority: {}, dueBefore: {}",
                keyword, status, priority, dueBefore);

        TaskSearchRequest request = TaskSearchRequest.builder()
                .keyword(keyword)
                .status(parseStatus(status))
                .priority(parsePriority(priority))
                .dueBefore(dueBefore)
                .page(page != null ? page : 0)
                .size(size != null ? size : 20)
                .build();

        return TaskSearchResult.from(taskService.searchTasks(request));
    }


    @Tool("Delete a task by its ID")
    public boolean deleteTask(@P("The ID of the task to delete") Long id) {
        log.info("Tool deleteTask called → id={}", id);
        return taskService.deleteTask(id);
    }

    @Tool("""
        Update an existing task.
        Required: id.
        Optional: title, description, status, priority, dueDate.
        """)
    public Optional<TaskResponse> updateTask(
            @P("The ID of the task to update - REQUIRED") Long id,
            @P("New title for the task - optional") String title,
            @P("New description for the task - optional") String description,
            @P("New status: TODO, IN_PROGRESS, or DONE - optional") String status,
            @P("New priority: HIGH, MEDIUM, or LOW - optional") String priority,
            @P("New due date - optional") LocalDateTime dueDate) {

        log.info("Tool updateTask called → id={}, title={}, status={}, priority={}",
                id, title, status, priority);

        TaskUpdateRequest updateRequest = TaskUpdateRequest.builder()
                .title(title)
                .description(description)
                .status(parseStatus(status))
                .priority(parsePriority(priority))
                .dueDate(dueDate)
                .build();

        return taskService.updateTask(id, updateRequest);
    }


    @Tool("Get the total count of tasks")
    public String getTaskCount() {
        log.debug("Getting task count");
        long count = taskService.getTaskCount();
        return "Total tasks: " + count;
    }

    @Tool("""
    Convert a relative date expression to LocalDateTime.
    
    Use this tool when the user says:
    - "today", "tomorrow", "yesterday"
    - "next Monday", "this Friday"
    - A specific date like "2026-03-04" or "03/04/2026"
    
    This tool returns the exact LocalDateTime you need for the addTask tool.
    """)
    public LocalDateTime parseDate(
            @P("The date expression from the user, e.g. 'tomorrow', '2026-03-04'") String dateExpression) {

        log.info("Tool parseDate called with: {}", dateExpression);

        LocalDateTime now = LocalDateTime.now();
        String normalized = dateExpression.toLowerCase().trim();

        // Handle relative dates
        if (normalized.equals("today")) {
            return now.toLocalDate().atTime(23, 59);
        }
        if (normalized.equals("tomorrow")) {
            return now.plusDays(1).toLocalDate().atTime(23, 59);
        }
        if (normalized.equals("yesterday")) {
            return now.minusDays(1).toLocalDate().atTime(23, 59);
        }

        // Handle explicit dates in various formats
        try {
            // Try YYYY-MM-DD
            if (normalized.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return LocalDate.parse(normalized).atTime(23, 59);
            }
            // Try DD/MM/YYYY or MM/DD/YYYY (assume MM/DD/YYYY for US format)
            if (normalized.matches("\\d{2}/\\d{2}/\\d{4}")) {
                String[] parts = normalized.split("/");
                return LocalDate.of(
                        Integer.parseInt(parts[2]), // year
                        Integer.parseInt(parts[0]), // month (US format)
                        Integer.parseInt(parts[1])  // day
                ).atTime(23, 59);
            }
        } catch (Exception e) {
            log.error("Failed to parse date: {}", dateExpression, e);
            throw new IllegalArgumentException("Could not parse date: " + dateExpression);
        }

        throw new IllegalArgumentException("Unrecognized date format: " + dateExpression);
    }


    private TaskStatus parseStatus(String status) {
        return status != null ? TaskStatus.valueOf(status.toUpperCase()) : null;
    }

    private TaskPriority parsePriority(String priority) {
        return priority != null ? TaskPriority.valueOf(priority.toUpperCase()) : null;
    }
}
