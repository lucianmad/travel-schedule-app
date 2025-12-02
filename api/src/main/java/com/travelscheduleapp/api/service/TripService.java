package com.travelscheduleapp.api.service;

import com.travelscheduleapp.api.dto.TripRequest;
import com.travelscheduleapp.api.dto.TripResponse;
import com.travelscheduleapp.api.entity.Trip;
import com.travelscheduleapp.api.exception.AccessDeniedException;
import com.travelscheduleapp.api.exception.ResourceNotFoundException;
import com.travelscheduleapp.api.mapper.TripMapper;
import com.travelscheduleapp.api.repository.TripRepository;
import com.travelscheduleapp.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;
    private final TripMapper tripMapper;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    public List<TripResponse> getAllTrips(Long userId) {
        var trips = tripRepository.findAllByUserId(userId);

        return trips.stream()
                .map(tripMapper::toResponseDto)
                .toList();
    }

    public TripResponse getTripById(Long id, Long userId) {
        var trip = tripRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip with id: " + id + " not found"));

        validateUser(trip, userId);

        return tripMapper.toResponseDto(trip);
    }

    @Transactional
    public TripResponse createTrip(Long userId, TripRequest tripRequest) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var trip = tripMapper.toEntity(tripRequest);

        trip.setUser(user);

        var savedTrip = tripRepository.save(trip);

        rabbitTemplate.convertAndSend("trip-generation-queue", savedTrip.getId());

        return tripMapper.toResponseDto(savedTrip);
    }

    @Transactional
    public TripResponse updateTrip(Long id, Long userId, TripRequest tripRequest) {
        var trip = tripRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        validateUser(trip, userId);

        tripMapper.updateTrip(trip, tripRequest);

        var savedTrip = tripRepository.save(trip);
        return tripMapper.toResponseDto(savedTrip);
    }

    public void deleteTrip(Long id, Long userId){
        var trip = tripRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        validateUser(trip, userId);

        tripRepository.delete(trip);
    }

    public void validateUser(Trip trip, Long userId) {
        if (!Objects.equals(trip.getUser().getId(), userId)) {
            throw new AccessDeniedException("Access Denied: You do not own this trip");
        }
    }
}
