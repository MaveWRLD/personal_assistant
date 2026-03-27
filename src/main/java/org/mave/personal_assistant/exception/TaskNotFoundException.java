package org.mave.personal_assistant.exception;

/**
 * Exception thrown when a task is not found by its ID.
 */
public class TaskNotFoundException extends RuntimeException {
    
    public TaskNotFoundException(Long taskId) {
        super("Task not found with ID: " + taskId);
    }
    
    public TaskNotFoundException(String message) {
        super(message);
    }
}
