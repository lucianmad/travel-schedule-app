package com.travelscheduleapp.api.repository;

import com.travelscheduleapp.api.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
