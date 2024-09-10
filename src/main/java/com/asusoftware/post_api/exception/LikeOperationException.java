package com.asusoftware.post_api.exception;

public class LikeOperationException extends RuntimeException {
    public LikeOperationException(String message) {
        super(message);
    }

    public LikeOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}