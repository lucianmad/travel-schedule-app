package com.travelscheduleapp.api.mapper;

import com.travelscheduleapp.api.dto.ActivityRequest;
import com.travelscheduleapp.api.dto.ActivityResponse;
import com.travelscheduleapp.api.entity.Activity;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {
    public Activity toEntity(ActivityRequest activityRequest) {
        var activity = new Activity();
        activity.setDayNumber(activityRequest.dayNumber());
        activity.setDayMoment(activityRequest.dayMoment());
        activity.setDescription(activityRequest.description());
        return activity;
    }

    public void updateEntity(Activity activity, ActivityRequest activityRequest) {
        activity.setDayNumber(activityRequest.dayNumber());
        activity.setDayMoment(activityRequest.dayMoment());
        activity.setDescription(activityRequest.description());
    }

    public ActivityResponse toResponseDto(Activity activity) {
        if (activity == null) {
            return null;
        }

        return new ActivityResponse(
                activity.getId(),
                activity.getTrip().getId(),
                activity.getDayNumber(),
                activity.getDayMoment(),
                activity.getDescription()
        );
    }
}
