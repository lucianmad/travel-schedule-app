package com.travelscheduleapp.api.dto;

import jakarta.validation.constraints.Min;

import java.util.List;

public record ActivityReorderRequest(
        @Min(value = 1, message = "Activity day number should be at least 1")
        int dayNumber,
        List<Long> activityIds
) {
}
