package com.travelscheduleapp.api.service;

import com.travelscheduleapp.api.dto.ActivityRequest;
import com.travelscheduleapp.api.dto.ActivityResponse;
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

    public List<ActivityResponse> getAllActivities() {
        var activities = activityRepository.findAll();

        return activities.stream()
                .map(activityMapper::toResponseDto)
                .toList();
    }

    public ActivityResponse getActivityById(Long id) {
        var activity = activityRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Activity with id: " + id + " not found"));

        return activityMapper.toResponseDto(activity);
    }

    @Transactional
    public ActivityResponse createActivity(ActivityRequest activityRequest) {
        var trip = tripRepository
                .findById(activityRequest.tripId())
                .orElseThrow(() -> new RuntimeException("Assigned trip not found"));

        var activity = activityMapper.toEntity(activityRequest);

        activity.setTrip(trip);

        var savedActivity = activityRepository.save(activity);
        return activityMapper.toResponseDto(savedActivity);
    }

    @Transactional
    public ActivityResponse updateActivity(Long id, ActivityRequest activityRequest) {
        var activity = activityRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        var trip = tripRepository
                .findById(activityRequest.tripId())
                .orElseThrow(() -> new RuntimeException("Assigned trip not found"));

        activityMapper.updateEntity(activity, activityRequest);

        activity.setTrip(trip);

        var savedActivity = activityRepository.save(activity);
        return activityMapper.toResponseDto(savedActivity);
    }

    public void deleteActivity(Long id) {
        var activity = activityRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        activityRepository.delete(activity);
    }
}
