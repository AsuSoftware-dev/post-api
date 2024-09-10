package com.asusoftware.post_api.exception;

public class PostUpdateException extends RuntimeException {
    public PostUpdateException(String message) {
        super(message);
    }

    public PostUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
