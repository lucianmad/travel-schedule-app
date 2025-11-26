package com.travelscheduleapp.api.dto;

public record ActivityResponse(
        Long id,
        Long tripId,
        int dayNumber,
        String dayMoment,
        String description
) {
}
