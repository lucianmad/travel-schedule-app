package com.travelscheduleapp.api.repository;

import com.travelscheduleapp.api.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByTripIdAndDayNumber(Long tripId, int dayNumber);
    List<Activity> findByTripIdOrderByDayNumberAscOrderIndexAsc(Long tripId);
}
