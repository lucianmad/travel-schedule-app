package com.travelscheduleapp.api.service;

import com.travelscheduleapp.api.dto.ActivityReorderRequest;
import com.travelscheduleapp.api.dto.ActivityRequest;
import com.travelscheduleapp.api.dto.ActivityResponse;
import com.travelscheduleapp.api.exception.AccessDeniedException;
import com.travelscheduleapp.api.exception.BadRequestException;
import com.travelscheduleapp.api.exception.ResourceNotFoundException;
import com.travelscheduleapp.api.mapper.ActivityMapper;
import com.travelscheduleapp.api.repository.ActivityRepository;
import com.travelscheduleapp.api.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final TripRepository tripRepository;
    private final ActivityMapper activityMapper;

    public List<ActivityResponse> getActivities(Long tripId, Long userId) {
        var trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        if (!trip.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        return activityRepository.findByTripIdOrderByDayNumberAscOrderIndexAsc(tripId).stream()
                .map(activityMapper::toResponseDto)
                .toList();
    }

    public ActivityResponse getActivityById(Long id, Long userId) {
        var activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        if (!activity.getTrip().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        return activityMapper.toResponseDto(activity);
    }

    @Transactional
    public ActivityResponse addActivity(Long tripId, Long userId, ActivityRequest activityRequest) {
        var trip = tripRepository
                .findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Assigned trip not found"));

        if (!trip.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        validateDayNumber(activityRequest.dayNumber(), trip.getNumberOfDays());

        var activity = activityMapper.toEntity(activityRequest);

        activity.setTrip(trip);

        var savedActivity = activityRepository.save(activity);
        return activityMapper.toResponseDto(savedActivity);
    }

    @Transactional
    public ActivityResponse updateActivity(Long id, Long userId, ActivityRequest activityRequest) {
        var activity = activityRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        var trip = activity.getTrip();

        if (!trip.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access Denied");
        }

        validateDayNumber(activityRequest.dayNumber(), trip.getNumberOfDays());

        activityMapper.updateEntity(activity, activityRequest);

        var savedActivity = activityRepository.save(activity);
        return activityMapper.toResponseDto(savedActivity);
    }

    @Transactional
    public void reorderActivities(Long tripId, Long userId, ActivityReorderRequest request) {
        var trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        if (!trip.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access Denied");
        }

        var activities = activityRepository.findByTripIdAndDayNumber(tripId, request.dayNumber());

        for (int i = 0; i < request.activityIds().size(); i++) {
            Long id = request.activityIds().get(i);

            var activity = activities.stream()
                    .filter(a -> a.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Activity Id mismatch"));

            activity.setOrderIndex(i);
        }

        activityRepository.saveAll(activities);
    }

    public void deleteActivity(Long id, Long userId) {
        var activity = activityRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        if (!activity.getTrip().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access Denied");
        }

        activityRepository.delete(activity);
    }

    private void validateDayNumber(int requestDay, int tripMaxDays) {
        if (requestDay > tripMaxDays) {
            throw new BadRequestException("Day number " + requestDay + " exceeds the trip duration of " + tripMaxDays + " days");
        }
    }
}
