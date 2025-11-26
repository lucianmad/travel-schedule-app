package com.travelscheduleapp.api.dto;

public record TripRequest(
        String destination,
        int numberOfDays
) {
}
