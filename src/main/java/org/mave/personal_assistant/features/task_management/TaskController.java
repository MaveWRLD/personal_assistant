package org.mave.personal_assistant.features.task_management;

import lombok.RequiredArgsConstructor;
import org.mave.personal_assistant.core.domain.Task;
import org.mave.personal_assistant.core.domain.enums.TaskPriority;
import org.mave.personal_assistant.features.task_management.dto.TaskCreateRequest;
import org.mave.personal_assistant.features.task_management.dto.TaskResponse;
import org.mave.personal_assistant.features.task_management.dto.TaskSearchRequest;
import org.mave.personal_assistant.features.task_management.dto.TaskUpdateRequest;
import org.mave.personal_assistant.core.domain.enums.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskCreateRequest createRequest) {
        log.info("Creating new task with title: {}", createRequest.getTitle());
        var createdTask = taskService.createTask(createRequest);
        return ResponseEntity.ok(createdTask);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        log.info("Retrieving all tasks");
        var tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        log.info("Retrieving task with ID: {}", id);
        var taskResponse = taskService.getTaskById(id);
        return ResponseEntity.ok(taskResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TaskResponse>> searchTasks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) LocalDateTime dueDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        TaskSearchRequest request = new TaskSearchRequest(keyword, status, priority, dueDate, page, size);
        return ResponseEntity.ok(taskService.searchTasks(request));
    }


    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody TaskUpdateRequest updateRequest) {
        log.info("Updating task with ID: {}", id);
        Optional<TaskResponse> updatedTask = taskService.updateTask(id, updateRequest);
        return updatedTask.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("Deleting task with ID: {}", id);
        boolean deleted = taskService.deleteTask(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


    @GetMapping("/count")
    public ResponseEntity<Long> getTaskCount() {
        log.info("Retrieving task count");
        long count = taskService.getTaskCount();
        return ResponseEntity.ok(count);
    }
}
