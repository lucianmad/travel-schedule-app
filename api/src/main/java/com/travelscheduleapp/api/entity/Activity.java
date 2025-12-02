package com.travelscheduleapp.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "activities")
@Data
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int dayNumber;

    @Column(nullable = false)
    private int orderIndex;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    @ToString.Exclude
    private Trip trip;
}
