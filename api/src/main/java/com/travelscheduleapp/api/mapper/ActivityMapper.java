package com.travelscheduleapp.api.mapper;

import com.travelscheduleapp.api.dto.ActivityCreateRequest;
import com.travelscheduleapp.api.dto.ActivityResponse;
import com.travelscheduleapp.api.entity.Activity;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {
    public Activity toEntity(ActivityCreateRequest activityRequest) {
        var activity = new Activity();
        activity.setDayNumber(activityRequest.dayNumber());
        activity.setDuration(activityRequest.duration());
        activity.setDescription(activityRequest.description());
        return activity;
    }

    public ActivityResponse toResponseDto(Activity activity) {
        if (activity == null) {
            return null;
        }

        return new ActivityResponse(
                activity.getId(),
                activity.getTrip().getId(),
                activity.getDayNumber(),
                activity.getOrderIndex(),
                activity.getDuration(),
                activity.getDescription()
        );
    }
}
