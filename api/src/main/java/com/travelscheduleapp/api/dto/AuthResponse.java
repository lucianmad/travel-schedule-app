package com.travelscheduleapp.api.dto;

public record AuthResponse(
        Long id,
        String email,
        String username
) {
}
