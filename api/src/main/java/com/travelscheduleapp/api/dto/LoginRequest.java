package com.travelscheduleapp.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email required")
        @Email(message = "Bad email format")
        String email,
        @NotBlank(message = "Password required")
        String password
) {
}
