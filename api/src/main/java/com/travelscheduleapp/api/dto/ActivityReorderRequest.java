package com.travelscheduleapp.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ActivityReorderRequest(
        @Min(value = 1, message = "Activity day number should be at least 1")
        int dayNumber,
        @NotEmpty(message = "Activity Ids required")
        List<Long> activityIds
) {
}
