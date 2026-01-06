package com.travelscheduleapp.api.controller;

import com.travelscheduleapp.api.dto.AuthResponse;
import com.travelscheduleapp.api.dto.LoginRequest;
import com.travelscheduleapp.api.dto.RegisterRequest;
import com.travelscheduleapp.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponse(responseCode = "201", description = "User successfully created")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "409", description = "Username or Email already exists")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        var response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in")
    @ApiResponse(responseCode = "200", description = "User successfully logged in")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "409", description = "Invalid credentials")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
