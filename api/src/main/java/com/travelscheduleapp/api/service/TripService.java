package com.travelscheduleapp.api.service;

import com.travelscheduleapp.api.dto.TripRequest;
import com.travelscheduleapp.api.dto.TripResponse;
import com.travelscheduleapp.api.mapper.TripMapper;
import com.travelscheduleapp.api.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    public List<TripResponse> getAllTrips() {
        var trips = tripRepository.findAll();

        return trips.stream()
                .map(tripMapper::toResponseDto)
                .toList();
    }

    public TripResponse getTripById(Long id) {
        var trip = tripRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Trip with id: " + id + " not found"));

        return tripMapper.toResponseDto(trip);
    }

    @Transactional
    public TripResponse createTrip(TripRequest tripRequest) {
        var trip = tripMapper.toEntity(tripRequest);

        var savedTrip = tripRepository.save(trip);
        return tripMapper.toResponseDto(savedTrip);
    }

    @Transactional
    public TripResponse updateTrip(Long id, TripRequest tripRequest) {
        var trip = tripRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        tripMapper.updateTrip(trip, tripRequest);

        var savedTrip = tripRepository.save(trip);
        return tripMapper.toResponseDto(savedTrip);
    }

    public void deleteTrip(Long id){
        var trip = tripRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        tripRepository.delete(trip);
    }
}
