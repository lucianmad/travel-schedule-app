package com.travelscheduleapp.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TripRequest(
        @NotBlank(message = "Destination is required")
        @Size(min = 2, max = 100, message = "Destination should have between 2 and 100 characters")
        String destination,
        @Min(value = 1, message = "Trip must have at least 1 day")
        @Max(value = 7, message = "Trip must have at most 7 days")
        int numberOfDays
) {
}
