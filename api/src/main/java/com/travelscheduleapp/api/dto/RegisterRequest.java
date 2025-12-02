package com.travelscheduleapp.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Email required")
        @Email(message = "Bad email format")
        String email,
        @NotBlank(message = "Username required")
        @Size(min = 3, max = 50, message = "Username should have between 3 and 50 characters")
        String username,
        @NotBlank(message = "Password required")
        @Size(min = 6, message = "Password should be at least 6 characters long")
        String password,
        @NotBlank(message = "Confirm password required")
        String confirmPassword
) {
}
