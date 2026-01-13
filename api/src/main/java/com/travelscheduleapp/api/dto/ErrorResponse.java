package com.travelscheduleapp.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
        @Schema(description = "The error message", example = "Resource not found")
        String error
) {
}
