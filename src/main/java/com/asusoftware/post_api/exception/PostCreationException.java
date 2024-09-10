package com.asusoftware.post_api.exception;

public class PostCreationException extends RuntimeException {
    public PostCreationException(String message) {
        super(message);
    }

    public PostCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}