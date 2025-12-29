package passvault.vaultservice.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import passvault.vaultservice.dto.ApiError;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
    return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request, Collections.emptyList());
  }

  @ExceptionHandler({ EncryptionException.class, IllegalArgumentException.class })
  public ResponseEntity<ApiError> handleBadRequest(RuntimeException ex, WebRequest request) {
    return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request, Collections.emptyList());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
    List<String> errors = ex.getBindingResult()
        .getAllErrors()
        .stream()
        .map(error -> {
          if (error instanceof FieldError fieldError) {
            return fieldError.getField() + ": " + fieldError.getDefaultMessage();
          }
          return error.getDefaultMessage();
        })
        .collect(Collectors.toList());
    return buildError(HttpStatus.BAD_REQUEST, "Validation failed", request, errors);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
    List<String> errors = ex.getConstraintViolations()
        .stream()
        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
        .collect(Collectors.toList());
    return buildError(HttpStatus.BAD_REQUEST, "Validation failed", request, errors);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneral(Exception ex, WebRequest request) {
    return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request, Collections.emptyList());
  }

  private ResponseEntity<ApiError> buildError(HttpStatus status, String message, WebRequest request,
      List<String> errors) {
    String path = request instanceof ServletWebRequest servletRequest
        ? servletRequest.getRequest().getRequestURI()
        : "";
    ApiError apiError = ApiError.builder()
        .status(status.value())
        .message(message)
        .errors(errors)
        .timestamp(Instant.now())
        .path(path)
        .build();
    return ResponseEntity.status(status).body(apiError);
  }
}
