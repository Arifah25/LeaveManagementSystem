package com.lms.LeaveManagementSystem.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.lms.LeaveManagementSystem.dto.LoginRequestDto;
import com.lms.LeaveManagementSystem.dto.RegisterRequestDto;
import com.lms.LeaveManagementSystem.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth Controller", description = "Authentication and authorization operations")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final AuthService authService;

    @PostMapping("/sign-up")
    @Operation(summary = "User Registration", description = "Allows a new user to register in the system")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto registerRequestDto) {
        try {
            authService.register(registerRequestDto);
            return ResponseEntity.ok("Register successfully");
        } catch (RuntimeException e) {
            if (e instanceof ResponseStatusException ex) {
                return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Allows a user to log in to the system")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            String token = authService.login(loginRequestDto);
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            if (e instanceof ResponseStatusException ex) {
                return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

    // Global Exception Handler for Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
