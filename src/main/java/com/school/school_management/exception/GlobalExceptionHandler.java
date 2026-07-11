package com.school.school_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

   @ExceptionHandler(MethodArgumentNotValidException.class)
   public ResponseEntity<Map<String, Object>> handleValidationErrors(
           MethodArgumentNotValidException ex) {

      Map<String, String> errors = new LinkedHashMap<>();
      for (FieldError error : ex.getBindingResult().getFieldErrors()) {
         errors.put(error.getField(), error.getDefaultMessage());
      }

      return ResponseEntity.badRequest().body(Map.of("errors", errors));
   }

   @ExceptionHandler(BadCredentialsException.class)
   public ResponseEntity<Map<String, String>> handleBadCredentials(
           BadCredentialsException ex) {

      return ResponseEntity
              .status(HttpStatus.UNAUTHORIZED)
              .body(Map.of("message", "Invalid username or password"));
   }

   @ExceptionHandler(AuthenticationException.class)
   public ResponseEntity<Map<String, String>> handleAuthentication(
           AuthenticationException ex) {

      return ResponseEntity
              .status(HttpStatus.UNAUTHORIZED)
              .body(Map.of("message", ex.getMessage()));
   }

   @ExceptionHandler(RuntimeException.class)
   public ResponseEntity<Map<String, String>> handleRuntimeException(
           RuntimeException ex) {

      String message = ex.getMessage() != null ? ex.getMessage() : "Request failed";

      if (message.contains("not found")) {
         return ResponseEntity
                 .status(HttpStatus.NOT_FOUND)
                 .body(Map.of("message", message));
      }

      if (message.contains("already exists")
              || message.contains("already taken")
              || message.contains("already registered")) {
         return ResponseEntity
                 .status(HttpStatus.CONFLICT)
                 .body(Map.of("message", message));
      }

      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(Map.of("message", message));
   }
}
