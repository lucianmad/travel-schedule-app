package com.travelscheduleapp.api.repository;

import com.travelscheduleapp.api.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
}
