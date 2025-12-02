package com.travelscheduleapp.api.dto;

public record ActivityResponse(
        Long id,
        Long tripId,
        int dayNumber,
        int orderIndex,
        String duration,
        String description
) {
}
