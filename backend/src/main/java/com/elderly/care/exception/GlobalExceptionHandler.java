package com.elderly.care.exception;

import com.elderly.care.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException ex) {
        log.error("Business exception: {}", ex.getMessage(), ex);
        Result<Void> result = Result.error(ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(result);
    }

    /**
     * 处理资源未找到异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Result<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        Result<Void> result = Result.notFound(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    /**
     * 处理验证异常
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Result<Void>> handleValidationException(ValidationException ex) {
        log.warn("Validation exception: {}", ex.getMessage());
        Result<Void> result = Result.badRequest(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理重复资源异常
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Result<Void>> handleDuplicateResourceException(DuplicateResourceException ex) {
        log.warn("Duplicate resource: {}", ex.getMessage());
        Result<Void> result = Result.error(ex.getCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
    }

    /**
     * 处理参数验证异常（@Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.warn("Method argument validation failed: {}", errors);
        Result<Map<String, String>> result = Result.badRequest("参数验证失败");
        result.setData(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Result<Void>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.warn("No handler found: {}", ex.getRequestURL());
        Result<Void> result = Result.notFound("请求的资源不存在");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        Result<Void> result = Result.badRequest(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Result<Void>> handleNullPointerException(NullPointerException ex) {
        log.error("Null pointer exception", ex);
        Result<Void> result = Result.error("系统内部错误");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理所有其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        Result<Void> result = Result.error("系统内部错误，请稍后重试");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}
