package com.travelscheduleapp.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "activities")
@Data
@EqualsAndHashCode(exclude = "trip")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int dayNumber;

    @Column(nullable = false)
    private int orderIndex;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private String duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    @ToString.Exclude
    @JsonIgnore
    private Trip trip;
}