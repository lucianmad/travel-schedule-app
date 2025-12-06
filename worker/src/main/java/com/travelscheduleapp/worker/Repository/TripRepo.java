package com.travelscheduleapp.worker.Repository;

import com.travelscheduleapp.worker.Entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepo extends JpaRepository<Trip, Long> {
}
