package org.mave.personal_assistant.exception;

/**
 * Exception thrown when task validation fails.
 */
public class TaskValidationException extends RuntimeException {
    
    public TaskValidationException(String message) {
        super(message);
    }
    
    public TaskValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
