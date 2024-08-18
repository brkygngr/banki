package com.brkygngr.banking.exception;

import com.brkygngr.banking.dto.ExceptionResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDateTime;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

  private final MessageSource messageSource;

  @ApiResponses(value = {
      @ApiResponse(responseCode = "400",
                   description = "Request validation errors.",
                   content = @Content(
                       mediaType = "application/json",
                       schema = @Schema(implementation = ExceptionResponse.class)))
  })
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValid(final MethodArgumentNotValidException exception) {
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
                   description = "User already exists.",
                   content = @Content(
                       mediaType = "application/json",
                       schema = @Schema(implementation = ExceptionResponse.class)))
  })
  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ExceptionResponse> handleUserAlreadyExists(final UserAlreadyExistsException exception) {
    String error = messageSource.getMessage(exception.getMessage(), null, Locale.ENGLISH);

    return ResponseEntity.badRequest()
                         .body(new ExceptionResponse(LocalDateTime.now(),
                                                     ExceptionCode.USER_ALREADY_EXISTS,
                                                     new String[]{error}));
  }

  @ApiResponses(value = {
      @ApiResponse(responseCode = "400",
                   description = "User or password invalid.",
                   content = @Content(
                       mediaType = "application/json",
                       schema = @Schema(implementation = ExceptionResponse.class)))
  })
  @ExceptionHandler(UserOrPasswordInvalidException.class)
  public ResponseEntity<ExceptionResponse> handleUserOrPasswordInvalid(final UserOrPasswordInvalidException exception) {
    String error = messageSource.getMessage(exception.getMessage(), null, Locale.ENGLISH);

    return ResponseEntity.badRequest()
                         .body(new ExceptionResponse(LocalDateTime.now(),
                                                     ExceptionCode.INVALID_REQUEST,
                                                     new String[]{error}));
  }

  @ApiResponses(value = {
      @ApiResponse(responseCode = "404",
                   description = "User not found.",
                   content = @Content(
                       mediaType = "application/json",
                       schema = @Schema(implementation = ExceptionResponse.class)))
  })
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ExceptionResponse> handleUserNotFound(final UserNotFoundException exception) {
    String error = messageSource.getMessage(exception.getMessage(), null, Locale.ENGLISH);

    return new ResponseEntity<>(new ExceptionResponse(LocalDateTime.now(),
                                                      ExceptionCode.USER_NOT_FOUND,
                                                      new String[]{error}), HttpStatus.NOT_FOUND);
  }

  @ApiResponses(value = {
      @ApiResponse(responseCode = "404",
                   description = "Account not found.",
                   content = @Content(
                       mediaType = "application/json",
                       schema = @Schema(implementation = ExceptionResponse.class)))
  })
  @ExceptionHandler(AccountNotFoundException.class)
  public ResponseEntity<ExceptionResponse> handleAccountNotFound(final AccountNotFoundException exception) {
    String error = messageSource.getMessage(exception.getMessage(), null, Locale.ENGLISH);

    return new ResponseEntity<>(new ExceptionResponse(LocalDateTime.now(),
                                                      ExceptionCode.USER_NOT_FOUND,
                                                      new String[]{error}),
                                HttpStatus.NOT_FOUND);
  }

  @ApiResponses(value = {
      @ApiResponse(responseCode = "400",
                   description = "Data values are already taken.",
                   content = @Content(
                       mediaType = "application/json",
                       schema = @Schema(implementation = ExceptionResponse.class)))
  })
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ExceptionResponse> handleDataIntegrityViolation(final DataIntegrityViolationException exception) {
    String error = messageSource.getMessage("app.resource.already.exists", null, Locale.ENGLISH);

    return ResponseEntity.badRequest()
                         .body(new ExceptionResponse(LocalDateTime.now(),
                                                     ExceptionCode.RESOURCE_ALREADY_EXISTS,
                                                     new String[]{error}));
  }

  @ApiResponses(value = {
      @ApiResponse(responseCode = "500",
                   description = "Unknown error.",
                   content = @Content(
                       mediaType = "application/json",
                       schema = @Schema(implementation = ExceptionResponse.class)))
  })
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionResponse> handleInternal(final Exception exception) {
    log.error("Internal server error: ", exception);

    String error = messageSource.getMessage("app.internal.server.error", null, Locale.ENGLISH);

    return ResponseEntity.internalServerError()
                         .body(new ExceptionResponse(LocalDateTime.now(),
                                                     ExceptionCode.USER_ALREADY_EXISTS,
                                                     new String[]{error}));
  }
}
