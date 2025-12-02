package com.travelscheduleapp.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActivityRequest(
        @NotBlank(message = "Day number required")
        @Min(value = 1, message = "Activity day number should be at least 1")
        int dayNumber,
        @NotBlank(message = "Order index required")
        @Min(value = 0, message = "Activity order index should be at least 0")
        int orderIndex,
        @NotBlank(message = "Duration required")
        @Size(min = 2, max = 50, message = "Duration should have between 2 and 50 characters")
        String duration,
        @NotBlank(message = "Description required")
        @Size(min = 5, message = "Description should have at least 5 characters")
        String description
) {
}
