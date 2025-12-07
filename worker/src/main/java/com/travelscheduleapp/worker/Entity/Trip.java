package com.travelscheduleapp.worker.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String destination;

    @Column(name = "number_of_days")
    private int numberOfDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "trip_status")
    private TripStatus tripStatus;
}