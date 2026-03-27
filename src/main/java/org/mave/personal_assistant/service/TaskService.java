package org.mave.personal_assistant.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.mave.personal_assistant.dto.requests.TaskUpdateRequest;
import org.mave.personal_assistant.exception.TaskNotFoundException;
import org.mave.personal_assistant.exception.TaskValidationException;
import org.mave.personal_assistant.models.Task;
import org.mave.personal_assistant.models.enums.TaskPriority;
import org.mave.personal_assistant.models.enums.TaskStatus;
import org.springframework.stereotype.Service;

/**
 * Service layer for managing tasks with in-memory storage.
 * Provides CRUD operations and business logic for task management.
 */
@Service
public class TaskService {
    
    private final ConcurrentHashMap<Long, Task> taskStorage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final TaskMapper taskMapper;
    
    public TaskService(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }
    
    /**
     * Creates a new task with the specified parameters.
     * 
     * @param title Task title (must not be null or empty)
     * @param description Task description (can be null)
     * @param priority Task priority (must not be null)
     * @param assignedTo Person assigned to the task (can be null)
     * @param dueDate Task due date (can be null)
     * @return The created task
     * @throws TaskValidationException if validation fails
     */
    public Task addTask(String title, String description, TaskPriority priority,
                       String assignedTo, LocalDateTime dueDate) {
        
        validateTaskCreation(title, priority);
        
        Task newTask = Task.builder()
                .id(idGenerator.getAndIncrement())
                .title(title)
                .description(description)
                .status(TaskStatus.TODO)
                .priority(priority)
                .dueDate(dueDate)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        taskStorage.put(newTask.getId(), newTask);
        return newTask;
    }
    
    /**
     * Retrieves a task by its ID.
     * 
     * @param id Task ID
     * @return Optional containing the task if found, empty otherwise
     */
    public Optional<Task> getTask(Long id) {
        return Optional.ofNullable(taskStorage.get(id));
    }
    
    /**
     * Deletes a task by its ID.
     * 
     * @param id Task ID
     * @return true if task was deleted, false if not found
     */
    public boolean deleteTask(Long id) {
        return taskStorage.remove(id) != null;
    }
    
    /**
     * Updates a task with the specified changes.
     * 
     * @param id Task ID
     * @param updateRequest Contains the fields to update
     * @return Optional containing the updated task if found, empty otherwise
     * @throws TaskNotFoundException if task not found
     * @throws TaskValidationException if validation fails
     */
    public Optional<Task> updateTask(Long id, TaskUpdateRequest updateRequest) {
        Task existingTask = taskStorage.get(id);
        if (existingTask == null) {
            return Optional.empty();
        }
        
        validateTaskUpdate(updateRequest);
        
        // Use MapStruct to update the existing task
        taskMapper.updateTaskFromDto(updateRequest, existingTask);
        existingTask.setUpdatedAt(LocalDateTime.now());
        
        return Optional.of(existingTask);
    }
    
    /**
     * Sets the priority of a task.
     * 
     * @param id Task ID
     * @param priority New priority
     * @return Optional containing the updated task if found, empty otherwise
     */
    public Optional<Task> setTaskPriority(Long id, TaskPriority priority) {
        if (priority == null) {
            throw new TaskValidationException("Priority cannot be null");
        }
        
        return updateTask(id, TaskUpdateRequest.builder().priority(priority).build());
    }
    
    /**
     * Sets the due date of a task.
     * 
     * @param id Task ID
     * @param dueDate New due date
     * @return Optional containing the updated task if found, empty otherwise
     */
    public Optional<Task> setTaskDueDate(Long id, LocalDateTime dueDate) {
        return updateTask(id, TaskUpdateRequest.builder().dueDate(dueDate).build());
    }
    
    /**
     * Sets the status of a task.
     * 
     * @param id Task ID
     * @param status New status
     * @return Optional containing the updated task if found, empty otherwise
     */
    public Optional<Task> setTaskStatus(Long id, TaskStatus status) {
        if (status == null) {
            throw new TaskValidationException("Status cannot be null");
        }
        
        return updateTask(id, TaskUpdateRequest.builder().status(status).build());
    }
    
    /**
     * Gets the total number of tasks.
     * 
     * @return Total task count
     */
    public long getTaskCount() {
        return taskStorage.size();
    }
    
    private void validateTaskCreation(String title, TaskPriority priority) {
        if (title == null || title.trim().isEmpty()) {
            throw new TaskValidationException("Task title cannot be null or empty");
        }
        if (priority == null) {
            throw new TaskValidationException("Task priority cannot be null");
        }
    }
    
    private void validateTaskUpdate(TaskUpdateRequest updateRequest) {
        if (updateRequest.getTitle() != null && updateRequest.getTitle().trim().isEmpty()) {
            throw new TaskValidationException("Task title cannot be null or empty");
        }
    }
    
}
