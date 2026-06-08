package com.kvanzi.jwtauth.app.handler;

import com.kvanzi.jwtauth.auth.api.exception.*;
import com.kvanzi.jwtauth.shared.api.ApiResponse;
import com.kvanzi.jwtauth.user.api.exception.UsernameTakenException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public @NonNull ResponseEntity<@NonNull ApiResponse<Void, UUID>> handleInternalError(Exception e) {
        UUID errorId = UUID.randomUUID();
        log.error("[{}] Unexpected error occurred", errorId, e);
        return ApiResponse.internalServerError("Unexpected error occurred. Please try again", errorId);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public @NonNull ResponseEntity<
        @NonNull ApiResponse<@NonNull Map<@NonNull String, @NonNull List<@NonNull String>>, Void>
        > handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<@NonNull String, @NonNull List<@NonNull String>> errors = e.getFieldErrors().stream()
            .filter(fieldError -> Objects.nonNull(fieldError.getDefaultMessage()))
            .collect(Collectors.groupingBy(
                FieldError::getField,
                Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
            ));

        return ApiResponse.badRequestWithData("Validation exception", errors);
    }

    @SuppressWarnings("NullableProblems")
    @ExceptionHandler(HandlerMethodValidationException.class)
    public @NonNull ResponseEntity<
        @NonNull ApiResponse<@NonNull Map<@NonNull String, @NonNull List<@NonNull String>>, Void>
        > handleHandlerMethodValidationException(
        HandlerMethodValidationException e) {
        Map<@NonNull String, @NonNull List<@NonNull String>> errors = e.getParameterValidationResults().stream()
            .filter(result -> result.getMethodParameter().getParameterName() != null)
            .collect(Collectors.toMap(
                result -> Objects.requireNonNull(result.getMethodParameter().getParameterName()),
                result -> result.getResolvableErrors().stream()
                    .flatMap(error -> Optional.ofNullable(error.getDefaultMessage()).stream())
                    .toList()
            ));

        return ApiResponse.badRequestWithData("Validation exception", errors);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public @NonNull ResponseEntity<@NonNull ApiResponse<Void, Void>> handleNoResourceFoundException() {
        return ApiResponse.notFound("Resource not found");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public @NonNull ResponseEntity<@NonNull ApiResponse<Void, Void>> handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException e) {
        return ApiResponse.badRequest("Invalid type for parameter '%s'".formatted(e.getName()));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<@NonNull ApiResponse<Void, Void>> handleAuthorizationDeniedException() {
        return ApiResponse.forbidden("Access denied");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<@NonNull ApiResponse<Void, Void>> handleHttpMessageNotReadableException(
        HttpMessageNotReadableException e) {
        String message = "Malformed JSON request. Please check the request body structure and data types";

        Throwable cause = e.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            String fieldName = invalidFormatException.getPath().stream()
                .map(JacksonException.Reference::getPropertyName)
                .reduce((f1, f2) -> f1 + "." + f2)
                .orElse("unknown_field");

            message = String.format("Invalid value provided for field '%s'", fieldName);
        }

        return ApiResponse.badRequest(message);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<@NonNull ApiResponse<Void, Void>> handleInvalidCredentialsException(
        InvalidCredentialsException e) {
        return ApiResponse.unauthorized(e.getMessage());
    }

    @ExceptionHandler(MissingCredentialsException.class)
    public ResponseEntity<@NonNull ApiResponse<Void, Void>> handleMissingCredentialsException(
        MissingCredentialsException e) {
        return ApiResponse.badRequest(e.getMessage());
    }

    @ExceptionHandler(MissingRefreshTokenException.class)
    public ResponseEntity<@NonNull ApiResponse<Void, Void>> handleMissingRefreshTokenException(
        MissingRefreshTokenException e) {
        return ApiResponse.badRequest(e.getMessage());
    }

    @ExceptionHandler({
        InvalidJwtTokenException.class,
        InvalidJwtTokenTypeException.class,
        JwtTokenExpiredException.class
    })
    public ResponseEntity<@NonNull ApiResponse<Void, Void>> handleRefreshTokenExceptions(RuntimeException e) {
        return ApiResponse.unauthorized(e.getMessage());
    }

    @ExceptionHandler(UsernameTakenException.class)
    public ResponseEntity<@NonNull ApiResponse<Void, Void>> handleUsernameTakenException(UsernameTakenException e) {
        return ApiResponse.badRequest(e.getMessage());
    }
}
