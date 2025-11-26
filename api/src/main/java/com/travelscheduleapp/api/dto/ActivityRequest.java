package com.travelscheduleapp.api.dto;

public record ActivityRequest(
        Long tripId,
        int dayNumber,
        String dayMoment,
        String description
) {
}
