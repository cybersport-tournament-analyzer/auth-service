package com.vkr.user_service.handler;

import com.vkr.user_service.exception.AuthException;
import com.vkr.user_service.exception.InvalidJwtException;
import com.vkr.user_service.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка ошибок валидации
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return e.getBindingResult().getAllErrors().stream()
                .collect(Collectors.groupingBy(error -> ((FieldError) error).getField(),
                        Collectors.mapping(error -> Objects.requireNonNullElse(error.getDefaultMessage(), ""),
                                Collectors.toList()))
                );
    }

    /**
     * Обработка ошибок при отсутствии пользователя
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(UserNotFoundException e, HttpServletRequest request) {
        log.error("Not found: {}", e.getMessage());
        return new ErrorResponse(e, request.getRequestURI());
    }

    /**
     * Обработка ошибок при попытке создать пользователя, который уже существует
     */
//    @ExceptionHandler(UserAlreadyExistsException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleNotFoundException(UserAlreadyExistsException e, HttpServletRequest request) {
//        log.error("User already exists: {}", e.getMessage());
//        return new ErrorResponse(e, request.getRequestURI());
//    }

    /**
     * Обработка ошибок при возникновении исключения времени выполнения
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("Runtime exception: {}", e.getMessage(), e);
        return new ErrorResponse(e, request.getRequestURI());
    }

    /**
     * Обработка ошибок аутентификации
     */
    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleDataValidationException(AuthException e, HttpServletRequest request) {
        log.error("Auth exception: {}", e.getMessage());
        return new ErrorResponse(e, request.getRequestURI());
    }

    /**
     * Обработка ошибок при невалидном JWT
     */
    @ExceptionHandler(InvalidJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleDataValidationException(InvalidJwtException e, HttpServletRequest request) {
        log.error("Invalid JWT exception: {}", e.getMessage());
        return new ErrorResponse(e, request.getRequestURI());
    }
}
