package org.mave.personal_assistant.features.task_management;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.mave.personal_assistant.features.task_management.dto.TaskCreateRequest;
import org.mave.personal_assistant.features.task_management.dto.TaskResponse;
import org.mave.personal_assistant.features.task_management.dto.TaskSearchRequest;
import org.mave.personal_assistant.features.task_management.dto.TaskUpdateRequest;
import org.mave.personal_assistant.core.domain.Task;
import org.mave.personal_assistant.core.domain.enums.TaskPriority;
import org.mave.personal_assistant.core.domain.enums.TaskStatus;
import org.mave.personal_assistant.shared.exception.TaskNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskManagementTool {

    private static final Logger log = LoggerFactory.getLogger(TaskManagementTool.class);
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
    public String addTask(
            @P("The clear title of task") String title,
            @P("Due date -  REQUIRED") LocalDateTime dueDate,
            @P("Priority: HIGH, MEDIUM, or LOW") String priority,
            @P("Optional additional details") String description,
            @P("Optional status: TODO, IN_PROGRESS, DONE") String status) {

        log.info("Tool addTask called → title={}, dueDate={}, priority={}, description={}",
                title, dueDate, priority, description);

        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle(title);
        request.setDueDate(dueDate);
        request.setPriority(TaskPriority.valueOf(priority != null ? priority.toUpperCase() : "MEDIUM"));

        if (description != null && !description.trim().isEmpty()) {
            request.setDescription(description);
        }
        if (status != null && !status.trim().isEmpty()) {
            request.setStatus(TaskStatus.valueOf(status.toUpperCase()));
        }

        try {
            var created = taskService.createTask(request);
            return "✅ Task created successfully!\n" +
                    "Title: " + created.getTitle() + "\n" +
                    "Due Date: " + dueDate + "\n" +
                    "Priority: " + request.getPriority() + "\n" +
                    "ID: " + created.getId();
        } catch (Exception e) {
            log.error("Failed to create task", e);
            return "❌ Failed to create task: " + e.getMessage();
        }
    }

    @Tool("Get all tasks")
    public List<TaskResponse> getAllTasks() {
        log.info("Tool getAllTasks called");
        try {
            return taskService.getAllTasks();
        } catch (Exception e) {
            log.error("Failed to get all tasks", e);
            return List.of();
        }
    }

    @Tool("Get a specific task by its ID")
    public TaskResponse getTaskById(@P("The ID of the task to retrieve") Long id) {
        log.info("Tool getTaskById called with id: {}", id);
        try {

            return taskService.getTaskById(id);
        } catch (Exception e) {
            log.error("Failed to get task with id: {}", id, e);
            return null;
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
    public String deleteTask(@P("The ID of the task to delete") Long id) {
        log.info("Tool deleteTask called with id: {}", id);
        try {
            boolean deleted = taskService.deleteTask(id);
            if (deleted) {
                return "✅ Task with ID " + id + " has been successfully deleted.";
            } else {
                return "⚠️ Task with ID " + id + " was not found.";
            }
        } catch (Exception e) {
            log.error("Failed to delete task with id: {}", id, e);
            return "❌ Failed to delete task: " + e.getMessage();
        }
    }

    @Tool("""
    Update an existing task.
    
    Provide the task ID and any fields you want to update:
    - title: New title for the task
    - description: New description
    - status: TODO, IN_PROGRESS, or DONE
    - priority: HIGH, MEDIUM, or LOW
    - dueDate: New due date in LocalDateTime format
    """)
    public TaskResponse updateTask(
            @P("The ID of the task to update") Long id,
            @P("New title for the task (optional)") String title,
            @P("New description for the task (optional)") String description,
            @P("New status: TODO, IN_PROGRESS, or DONE (optional)") String status,
            @P("New priority: HIGH, MEDIUM, or LOW (optional)") String priority,
            @P("New due date (optional)") LocalDateTime dueDate) {

        log.info("Tool updateTask called with id: {}, title: {}, status: {}, priority: {}",
                id, title, status, priority);

        TaskUpdateRequest updateRequest = TaskUpdateRequest.builder()
                .title(title)
                .description(description)
                .status(parseStatus(status))
                .priority(parsePriority(priority))
                .dueDate(dueDate)
                .build();

        return taskService.updateTask(id, updateRequest)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
    }


    @Tool("Get the total number of tasks")
    public String getTaskCount() {
        log.info("Tool getTaskCount called");
        try {
            long count = taskService.getTaskCount();
            return "📊 Total number of tasks: " + count;
        } catch (Exception e) {
            log.error("Failed to get task count", e);
            return "❌ Failed to get task count: " + e.getMessage();
        }
    }

    private String getUpdatedFields(TaskUpdateRequest request) {
        StringBuilder fields = new StringBuilder();
        if (request.getTitle() != null) fields.append("title, ");
        if (request.getDescription() != null) fields.append("description, ");
        if (request.getStatus() != null) fields.append("status, ");
        if (request.getPriority() != null) fields.append("priority, ");
        if (request.getDueDate() != null) fields.append("dueDate, ");
        
        if (!fields.isEmpty()) {
            return fields.substring(0, fields.length() - 2);
        }
        return "none";
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
