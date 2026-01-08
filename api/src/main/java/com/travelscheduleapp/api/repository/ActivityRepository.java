package com.travelscheduleapp.api.repository;

import com.travelscheduleapp.api.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByTripIdOrderByDayNumberAscOrderIndexAsc(Long tripId);
    @Query("SELECT COALESCE(MAX(a.orderIndex), -1) FROM Activity a WHERE a.trip.id = :tripId AND a.dayNumber = :dayNumber")
    Integer findMaxOrderIndex(@Param("tripId") Long tripId, @Param("dayNumber") int dayNumber);
}
