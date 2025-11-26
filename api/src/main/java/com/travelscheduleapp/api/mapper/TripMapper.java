package com.travelscheduleapp.api.mapper;

import com.travelscheduleapp.api.TripStatus;
import com.travelscheduleapp.api.dto.TripRequest;
import com.travelscheduleapp.api.dto.TripResponse;
import com.travelscheduleapp.api.entity.Trip;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TripMapper {
    private final ActivityMapper activityMapper;

    public Trip toEntity(TripRequest tripRequest) {
        var trip = new Trip();
        trip.setDestination(tripRequest.destination());
        trip.setNumberOfDays(tripRequest.numberOfDays());
        trip.setTripStatus(TripStatus.PENDING);
        return trip;
    }

    public void updateTrip(Trip trip, TripRequest tripRequest) {
        trip.setDestination(tripRequest.destination());
        trip.setNumberOfDays(tripRequest.numberOfDays());
    }

    public TripResponse toResponseDto(Trip trip) {
        if (trip == null) {
            return null;
        }

        return new TripResponse(
                trip.getId(),
                trip.getDestination(),
                trip.getNumberOfDays(),
                trip.getTripStatus().toString(),
                trip.getActivityList().stream()
                        .map(activityMapper::toResponseDto)
                        .toList()
        );
    }
}
