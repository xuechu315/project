package com.elderly.care.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ValidationException extends BusinessException {

    public ValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public ValidationException(String field, String message) {
        super(HttpStatus.BAD_REQUEST, 
              String.format("Validation failed for field '%s': %s", field, message));
    }
}
