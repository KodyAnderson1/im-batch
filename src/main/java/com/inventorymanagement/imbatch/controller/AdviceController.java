package com.inventorymanagement.imbatch.controller;

import com.inventorymanagement.imbatch.exception.AuthorizationException;
import com.inventorymanagement.imbatch.exception.BadRequestException;
import com.inventorymanagement.imbatch.models.Status;
import com.inventorymanagement.imbatch.utilities.ConsoleFormatter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static com.inventorymanagement.imbatch.utilities.ConsoleFormatter.printColored;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequestMapping(produces = "application/json")
public class AdviceController {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Status.class)))
  private ResponseEntity<Status> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException ex) {
    Status errorResponse = Status.builder()
            .status(HttpStatus.BAD_REQUEST.toString())
            .message("Invalid JSON")
            .build();

    printColored(String.format("HttpMessageNotReadableException: %s", ex.getMessage()), ConsoleFormatter.Color.RED);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(BadRequestException.class)
  @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Status.class)))
  private ResponseEntity<Status> badRequestExceptionHandler(BadRequestException ex) {
    Status errorResponse = Status.builder()
            .status(HttpStatus.BAD_REQUEST.toString())
            .message(ex.getMessage())
            .build();

    printColored(String.format("BadRequestException: %s", ex.getMessage()), ConsoleFormatter.Color.RED);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Status.class)))
  private ResponseEntity<Status> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException ex) {
    Status errorResponse = Status.builder()
            .status(HttpStatus.BAD_REQUEST.toString())
            .message(String.format("The parameter '%s' of value '%s' could not be converted.",
                    ex.getName(), ex.getValue()))
            .build();

    printColored(String.format("The parameter: '%s' value: '%s' could not be converted.", ex.getName(), ex.getValue()), ConsoleFormatter.Color.RED, false);
    printColored(String.format("MethodArgumentTypeMismatchException: %s", ex.getMessage()), ConsoleFormatter.Color.RED);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalArgumentException.class)
  @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Status.class)))
  private ResponseEntity<Status> illegalArgumentExceptionHandler(IllegalArgumentException ex) {
    Status errorResponse = Status.builder()
            .status(HttpStatus.BAD_REQUEST.toString())
            .message(ex.getMessage())
            .build();

    printColored(String.format("IllegalArgumentException: %s", ex.getMessage()), ConsoleFormatter.Color.RED);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(AuthorizationException.class)
  @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Status.class)))
  private ResponseEntity<Status> authorizationExceptionHandler(AuthorizationException ex) {
    Status errorResponse = Status.builder()
            .status(HttpStatus.UNAUTHORIZED.toString())
            .message(HttpStatus.UNAUTHORIZED.getReasonPhrase())
            .build();

    printColored(String.format("AuthorizationException: %s", ex.getMessage()), ConsoleFormatter.Color.RED);
    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(AccessDeniedException.class)
  @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Status.class)))
  private ResponseEntity<Status> accessDeniedExceptionHandler(AccessDeniedException ex) {
    Status errorResponse = Status.builder()
            .status(HttpStatus.UNAUTHORIZED.toString())
            .message(HttpStatus.UNAUTHORIZED.getReasonPhrase())
            .build();

    printColored(String.format("accessDeniedExceptionHandler: %s", ex.getMessage()), ConsoleFormatter.Color.RED);
    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(RuntimeException.class)
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Status.class)))
  private ResponseEntity<Status> runtimeExceptionHandler(RuntimeException ex) {
    // print exception class name
    printColored(ex.getClass().getName(), ConsoleFormatter.Color.PURPLE);



    Status errorResponse = Status.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
            .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .build();

    printColored(String.format("RuntimeException: %s", ex.getMessage()), ConsoleFormatter.Color.RED);
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Status.class)))
  private ResponseEntity<Status> exceptionHandler(Exception ex) {
    Status errorResponse = Status.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
            .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .build();

    printColored(String.format("Exception: %s", ex.getMessage()), ConsoleFormatter.Color.RED);
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
