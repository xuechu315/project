package com.elderly.care.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    
    private final Integer code;
    private final HttpStatus httpStatus;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public BusinessException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = httpStatus.value();
    }

    public BusinessException(Integer code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }
}
