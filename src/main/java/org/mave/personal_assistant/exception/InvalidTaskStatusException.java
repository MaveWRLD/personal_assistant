package org.mave.personal_assistant.exception;

/**
 * Exception thrown when an invalid task status transition is attempted.
 */
public class InvalidTaskStatusException extends RuntimeException {
    
    public InvalidTaskStatusException(String message) {
        super(message);
    }
}
