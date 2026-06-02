package com.example.elderlycare.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String message) {
        super(HttpStatus.CONFLICT, message);
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(HttpStatus.CONFLICT,
              String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
