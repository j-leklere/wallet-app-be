package com.walletapp.shared.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ErrorResponse.of(HttpStatus.UNAUTHORIZED, ex.getMessage()));
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ErrorResponse.of(HttpStatus.CONFLICT, ex.getMessage()));
  }

  @ExceptionHandler(EntityInUseException.class)
  public ResponseEntity<ErrorResponse> handleEntityInUse(EntityInUseException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ErrorResponse.of(HttpStatus.CONFLICT, ex.getMessage()));
  }

  @ExceptionHandler(InvalidPasswordException.class)
  public ResponseEntity<ErrorResponse> handleInvalidPassword(InvalidPasswordException ex) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage()));
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage()));
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> fieldErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(
                    f -> f.getField(),
                    f -> f.getDefaultMessage() != null ? f.getDefaultMessage() : "Invalid value",
                    (first, second) -> first));
    return ResponseEntity.badRequest()
        .body(ErrorResponse.ofValidation(HttpStatus.BAD_REQUEST, "Validation failed", fieldErrors));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, "Malformed or unreadable request body"));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    String message =
        String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName());
    return ResponseEntity.badRequest().body(ErrorResponse.of(HttpStatus.BAD_REQUEST, message));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
    String errorId = UUID.randomUUID().toString();
    log.error("Unexpected error [{}]: {}", errorId, ex.getMessage(), ex);
    return ResponseEntity.internalServerError()
        .body(
            ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error. Reference: " + errorId));
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public record ErrorResponse(
      int status, String message, LocalDateTime timestamp, Map<String, String> fieldErrors) {

    static ErrorResponse of(HttpStatus status, String message) {
      return new ErrorResponse(status.value(), message, LocalDateTime.now(), null);
    }

    static ErrorResponse ofValidation(
        HttpStatus status, String message, Map<String, String> fieldErrors) {
      return new ErrorResponse(status.value(), message, LocalDateTime.now(), fieldErrors);
    }
  }
}
