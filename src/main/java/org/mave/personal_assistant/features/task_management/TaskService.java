package org.mave.personal_assistant.features.task_management;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mave.personal_assistant.core.domain.Task;
import org.mave.personal_assistant.core.domain.enums.TaskStatus;
import org.mave.personal_assistant.features.task_management.dto.TaskCreateRequest;
import org.mave.personal_assistant.features.task_management.dto.TaskSearchRequest;
import org.mave.personal_assistant.features.task_management.dto.TaskUpdateRequest;
import org.mave.personal_assistant.features.task_management.dto.TaskResponse;
import org.mave.personal_assistant.shared.exception.TaskNotFoundException;
import org.mave.personal_assistant.shared.exception.TaskValidationException;
import org.mave.personal_assistant.features.task_management.mapper.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * Service layer for managing tasks with database persistence.
 * Provides CRUD operations and business logic for task management.
 */
@Slf4j
@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;


    /**
     * Creates a new task using TaskCreateRequest DTO.
     *
     * @param createRequest DTO with task creation details
     * @return The created task
     * @throws TaskValidationException if validation fails
     */
    public TaskResponse createTask(TaskCreateRequest createRequest) {
        log.info("Creating new task with title: {}", createRequest.getTitle());
        validateTaskCreation(createRequest);

        Task newTask = taskMapper.toEntity(createRequest);

        if (newTask.getStatus() == null) {
            newTask.setStatus(TaskStatus.TODO);
        }

        Task savedTask = taskRepository.save(newTask);
        log.info("Task created successfully with ID: {}, title: {}", savedTask.getId(), savedTask.getTitle());
        return taskMapper.toResponse(savedTask);
    }

    /**
     * Retrieves all tasks.
     * @return List of TaskResponse
     */
    public List<TaskResponse> getAllTasks() {
        log.info("Retrieving all tasks");
        return taskRepository.findAll().stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id Task ID
     * @return Optional containing the task if found, empty otherwise
     */
    public TaskResponse getTaskById(Long id) {
        log.debug("Retrieving task with ID: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return taskMapper.toResponse(task);
    }

    /**
     * Searches for tasks based on the provided criteria.
     * @param request TaskSearchRequest containing search criteria
    * */
    public Page<TaskResponse> searchTasks(TaskSearchRequest request) {
        Specification<Task> spec = Specification
                .where(TaskSpecification.hasKeyword(request.getKeyword()))
                .and(TaskSpecification.hasStatus(request.getStatus()))
                .and(TaskSpecification.hasPriority(request.getPriority()))
                .and(TaskSpecification.dueBefore(request.getDueBefore()));

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return taskRepository.findAll(spec, pageable)
                .map(taskMapper::toResponse);
    }

    /**
     * Deletes a task by its ID.
     *
     * @param id Task ID
     * @return true if task was deleted, false if not found
     */
    public boolean deleteTask(Long id) {
        log.info("Attempting to delete task with ID: {}", id);
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            log.info("Task deleted successfully with ID: {}", id);
            return true;
        } else {
            log.warn("Failed to delete task - not found with ID: {}", id);
            return false;
        }
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
    public Optional<TaskResponse> updateTask(Long id, TaskUpdateRequest updateRequest) {
        log.info("Updating task with ID: {}", id);
        Optional<Task> existingTaskOpt = taskRepository.findById(id);
        if (existingTaskOpt.isEmpty()) {
            log.warn("Task not found for update with ID: {}", id);
            return Optional.empty();
        }

        Task existingTask = existingTaskOpt.get();
        validateTaskUpdate(updateRequest);

        taskMapper.updateTaskFromDto(updateRequest, existingTask);
        existingTask.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(existingTask);
        log.info("Task updated successfully with ID: {}, title: {}", updatedTask.getId(), updatedTask.getTitle());
        return Optional.of(taskMapper.toResponse(updatedTask));
    }

    /**
     * Gets the total number of tasks.
     *
     * @return Total task count
     */
    public long getTaskCount() {
        return taskRepository.count();
    }

    private void validateTaskCreation(TaskCreateRequest createRequest) {
        if (createRequest.getTitle() == null || createRequest.getTitle().trim().isEmpty()) {
            throw new TaskValidationException("Task title cannot be null or empty");
        }
        if (createRequest.getPriority() == null) {
            throw new TaskValidationException("Task priority cannot be null");
        }
    }
    
    private void validateTaskUpdate(TaskUpdateRequest updateRequest) {
        if (updateRequest.getTitle() != null && updateRequest.getTitle().trim().isEmpty()) {
            throw new TaskValidationException("Task title cannot be null or empty");
        }
    }
    
}
