package com.asusoftware.post_api.exception;

public class CommentOperationException extends RuntimeException {
    public CommentOperationException(String message) {
        super(message);
    }

    public CommentOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}