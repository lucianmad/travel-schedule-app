package com.travelscheduleapp.api.dto;

import java.util.List;

public record TripResponse(
        Long id,
        String destination,
        int numberOfDays,
        String status,
        List<ActivityResponse> activities
){
}
