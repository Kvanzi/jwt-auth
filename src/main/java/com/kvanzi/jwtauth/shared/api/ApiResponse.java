package com.kvanzi.jwtauth.shared.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.time.Instant;
import java.util.Objects;

/**
 * @param <D> Data type
 * @param <M> Meta type
 */
@Getter
public class ApiResponse<D, M> {

    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String reasonPhrase;

    @NonNull
    private final String message;

    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final M meta;

    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final D data;

    @NonNull
    private final Instant timestamp;


    @NonNull
    @JsonIgnore
    private final HttpStatus status;

    private static final String DEFAULT_SUCCESS_MESSAGE = "Success";

    public ApiResponse(
            @NonNull HttpStatus status,
            @NonNull String message,
            @Nullable M meta,
            @Nullable D data
    ) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.message = Objects.requireNonNull(message, "Message cannot be null");
        this.reasonPhrase = status.isError() ? status.getReasonPhrase() : null;
        this.meta = meta;
        this.data = data;
        this.timestamp = Instant.now();
    }

    public static <D> ResponseEntity<@NonNull ApiResponse<@NonNull D, @Nullable Void>> success(
            @NonNull D data
    ) {
        return success(HttpStatus.OK, DEFAULT_SUCCESS_MESSAGE, data);
    }

    public static <D> ResponseEntity<@NonNull ApiResponse<@NonNull D, @Nullable Void>> success(
            @NonNull String message,
            @NonNull D data
    ) {
        return success(HttpStatus.OK, message, data);
    }

    public static <D> ResponseEntity<@NonNull ApiResponse<@NonNull D, @Nullable Void>> success(
            @NonNull HttpStatus status,
            @NonNull String message,
            @Nullable D data
    ) {
        validateSuccessStatus(status);
        ApiResponse<D, Void> response = new ApiResponse<>(status, message, null, data);
        return ResponseEntity.status(status).body(response);
    }

    public static <D> ResponseEntity<@NonNull ApiResponse<@NonNull D, @Nullable Void>> success(
            @NonNull HttpStatus status,
            @NonNull String message,
            @Nullable D data,
            @Nullable URI location
    ) {
        validateSuccessStatus(status);
        ApiResponse<D, Void> response = new ApiResponse<>(status, message, null, data);

        ResponseEntity.BodyBuilder builder = ResponseEntity.status(status);
        if (location != null) {
            builder.location(location);
        }

        return builder.body(response);
    }

    public static <D, M> ResponseEntity<@NonNull ApiResponse<@NonNull D, @NonNull M>> successWithMeta(
            @NonNull D data,
            @NonNull M meta
    ) {
        return successWithMeta(HttpStatus.OK, DEFAULT_SUCCESS_MESSAGE, data, meta);
    }

    public static <D, M> ResponseEntity<@NonNull ApiResponse<@NonNull D, @NonNull M>> successWithMeta(
            @NonNull String message,
            @NonNull D data,
            @NonNull M meta
    ) {
        return successWithMeta(HttpStatus.OK, message, data, meta);
    }

    public static <D, M> ResponseEntity<@NonNull ApiResponse<@NonNull D, @NonNull M>> successWithMeta(
            @NonNull HttpStatus status,
            @NonNull String message,
            @NonNull D data,
            @NonNull M meta
    ) {
        validateSuccessStatus(status);
        ApiResponse<D, M> response = new ApiResponse<>(status, message, meta, data);
        return ResponseEntity.status(status).body(response);
    }

    public static <M> ResponseEntity<@NonNull ApiResponse<@Nullable Void, @NonNull M>> successOnlyMeta(
            @NonNull M meta
    ) {
        return successOnlyMeta(HttpStatus.OK, DEFAULT_SUCCESS_MESSAGE, meta);
    }

    public static <M> ResponseEntity<@NonNull ApiResponse<@Nullable Void, @NonNull M>> successOnlyMeta(
            @NonNull String message,
            @NonNull M meta
    ) {
        return successOnlyMeta(HttpStatus.OK, message, meta);
    }

    public static <M> ResponseEntity<@NonNull ApiResponse<@Nullable Void, @NonNull M>> successOnlyMeta(
            @NonNull HttpStatus status,
            @NonNull String message,
            @NonNull M meta
    ) {
        validateSuccessStatus(status);
        ApiResponse<Void, M> response = new ApiResponse<>(status, message, meta, null);
        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<@NonNull ApiResponse<@Nullable Void, @Nullable Void>> successNoContent() {
        return successNoContent(DEFAULT_SUCCESS_MESSAGE);
    }

    public static ResponseEntity<@NonNull ApiResponse<@Nullable Void, @Nullable Void>> successNoContent(
            @NonNull String message
    ) {
        return successNoContent(HttpStatus.OK, message);
    }

    public static ResponseEntity<@NonNull ApiResponse<@Nullable Void, @Nullable Void>> successNoContent(
            @NonNull HttpStatus status,
            @NonNull String message
    ) {
        validateSuccessStatus(status);
        ApiResponse<Void, Void> response = new ApiResponse<>(status, message, null, null);
        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<@NonNull ApiResponse<@Nullable Void, @Nullable Void>> successNoContent(
            @NonNull HttpStatus status,
            @NonNull String message,
            @Nullable URI location
    ) {
        validateSuccessStatus(status);
        ApiResponse<Void, Void> response = new ApiResponse<>(status, message, null, null);

        ResponseEntity.BodyBuilder builder = ResponseEntity.status(status);
        if (location != null) {
            builder.location(location);
        }

        return builder.body(response);
    }

    public static <D> ResponseEntity<@NonNull ApiResponse<@NonNull D, @Nullable Void>> created(
            @NonNull D data,
            @NonNull URI location
    ) {
        return success(HttpStatus.CREATED, "Resource created", data, location);
    }

    public static ResponseEntity<@NonNull ApiResponse<@Nullable Void, @Nullable Void>> created(
            @NonNull URI location
    ) {
        return successNoContent(HttpStatus.CREATED, "Resource created", location);
    }

    public static <D> ResponseEntity<@NonNull ApiResponse<@NonNull D, @Nullable Void>> created(
            @NonNull String message,
            @NonNull D data,
            @NonNull URI location
    ) {
        return success(HttpStatus.CREATED, message, data, location);
    }

    public static <D, M> ResponseEntity<@NonNull ApiResponse<@NonNull D, @NonNull M>> createdWithMeta(
            @NonNull D data,
            @NonNull M meta
    ) {
        return successWithMeta(HttpStatus.CREATED, "Resource created", data, meta);
    }

    public static ResponseEntity<@NonNull ApiResponse<@Nullable Void, @Nullable Void>> error(
            @NonNull HttpStatus status,
            @NonNull String message
    ) {
        validateErrorStatus(status);
        ApiResponse<Void, Void> response = new ApiResponse<>(status, message, null, null);
        return ResponseEntity.status(status).body(response);
    }

    public static <D> ResponseEntity<@NonNull ApiResponse<@NonNull D, @Nullable Void>> errorWithData(
            @NonNull HttpStatus status,
            @NonNull String message,
            @NonNull D errorData
    ) {
        validateErrorStatus(status);
        ApiResponse<D, Void> response = new ApiResponse<>(status, message, null, errorData);
        return ResponseEntity.status(status).body(response);
    }

    public static <M> ResponseEntity<@NonNull ApiResponse<@Nullable Void, @NonNull M>> errorWithMeta(
            @NonNull HttpStatus status,
            @NonNull String message,
            @NonNull M meta
    ) {
        validateErrorStatus(status);
        ApiResponse<Void, M> response = new ApiResponse<>(status, message, meta, null);
        return ResponseEntity.status(status).body(response);
    }

    public static <D, M> ResponseEntity<@NonNull ApiResponse<@NonNull D, @NonNull M>> errorFull(
            @NonNull HttpStatus status,
            @NonNull String message,
            @NonNull D data,
            @NonNull M meta
    ) {
        validateErrorStatus(status);
        ApiResponse<D, M> response = new ApiResponse<>(status, message, meta, data);
        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<@NonNull ApiResponse<@Nullable Void, @Nullable Void>> badRequest(
            @NonNull String message
    ) {
        return error(HttpStatus.BAD_REQUEST, message);
    }

    public static <D> ResponseEntity<@NonNull ApiResponse<@NonNull D, @Nullable Void>> badRequestWithData(
            @NonNull String message,
            @NonNull D data
    ) {
        return errorWithData(HttpStatus.BAD_REQUEST, message, data);
    }

    public static <M> ResponseEntity<@NonNull ApiResponse<@NonNull Void, @Nullable M>> badRequestWithMeta(
            @NonNull String message,
            @NonNull M meta
    ) {
        return errorWithMeta(HttpStatus.BAD_REQUEST, message, meta);
    }

    public static ResponseEntity<@NonNull ApiResponse<@Nullable Void, @Nullable Void>> notFound(
            @NonNull String message
    ) {
        return error(HttpStatus.NOT_FOUND, message);
    }

    public static ResponseEntity<@NonNull ApiResponse<@Nullable Void, @Nullable Void>> unauthorized(
            @NonNull String message
    ) {
        return error(HttpStatus.UNAUTHORIZED, message);
    }

    public static ResponseEntity<@NonNull ApiResponse<@Nullable Void, @Nullable Void>> forbidden(
            @NonNull String message
    ) {
        return error(HttpStatus.FORBIDDEN, message);
    }

    public static ResponseEntity<@NonNull ApiResponse<@Nullable Void, @Nullable Void>> conflict(
            @NonNull String message
    ) {
        return error(HttpStatus.CONFLICT, message);
    }

    public static ResponseEntity<@NonNull ApiResponse<@Nullable Void, @Nullable Void>> internalServerError(
            @NonNull String message
    ) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public static <M> ResponseEntity<@NonNull ApiResponse<@Nullable Void, @NonNull M>> internalServerError(
            @NonNull String message,
            @NonNull M meta
    ) {
        return errorWithMeta(HttpStatus.INTERNAL_SERVER_ERROR, message, meta);
    }

    public static ResponseEntity<@NonNull ApiResponse<@Nullable Void, @Nullable Void>> notImplemented(
            @NonNull String message
    ) {
        return error(HttpStatus.NOT_IMPLEMENTED, message);
    }

    private static void validateSuccessStatus(@NonNull HttpStatus status) {
        if (!status.is2xxSuccessful()) {
            throw new IllegalArgumentException("Invalid status for success response: " + status);
        }
    }

    private static void validateErrorStatus(@NonNull HttpStatus status) {
        if (!status.isError()) {
            throw new IllegalArgumentException("Invalid status for error response: " + status);
        }
    }

    @Override
    public String toString() {
        return "ApiResponse:\n{\n\tstatusCode=%d,\n\tmessage='%s',\n\thasData=%s,\n\thasMeta=%s,\n\ttimestamp=%s\n}"
                .formatted(
                        status.value(),
                        message,
                        data != null,
                        meta != null,
                        timestamp
                );
    }
}