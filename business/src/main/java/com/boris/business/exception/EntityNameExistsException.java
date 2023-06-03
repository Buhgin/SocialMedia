package com.boris.business.exception;

public class EntityNameExistsException extends RuntimeException {
    public EntityNameExistsException(String message) {
        super(message);
    }
}
