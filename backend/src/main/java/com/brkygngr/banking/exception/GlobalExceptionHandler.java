package com.brkygngr.banking.exception;

import com.brkygngr.banking.dto.ExceptionResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDateTime;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

  private final MessageSource messageSource;

  @ApiResponses(value = {
      @ApiResponse(responseCode = "400",
          description = "Request validation errors",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ExceptionResponse.class)))
  })
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionResponse> handleBadRequest(final MethodArgumentNotValidException exception) {
    String[] errors = exception.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .toArray(String[]::new);

    return ResponseEntity.badRequest()
        .body(new ExceptionResponse(LocalDateTime.now(), ExceptionCode.INVALID_REQUEST, errors));
  }

  @ApiResponses(value = {
      @ApiResponse(responseCode = "400",
          description = "User already exists",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ExceptionResponse.class)))
  })
  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ExceptionResponse> handleBadRequest(final UserAlreadyExistsException exception) {
    String error = messageSource.getMessage(exception.getMessage(), null, Locale.ENGLISH);

    return ResponseEntity.badRequest()
        .body(new ExceptionResponse(LocalDateTime.now(), ExceptionCode.INVALID_REQUEST, new String[]{error}));
  }
}
