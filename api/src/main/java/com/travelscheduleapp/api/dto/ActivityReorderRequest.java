package com.travelscheduleapp.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ActivityReorderRequest(
        @NotBlank(message = "Day number required")
        @Min(value = 1, message = "Activity day number should be at least 1")
        int dayNumber,
        List<Long> activityIds
) {
}
