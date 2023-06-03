package com.boris.business.exception;

public class BlogApiException extends RuntimeException {
    public BlogApiException(String message) {
        super(message);
    }
}
