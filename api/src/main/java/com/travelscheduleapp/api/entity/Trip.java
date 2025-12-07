package com.travelscheduleapp.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trips")
@Data
@EqualsAndHashCode(exclude = {"activityList", "user"})
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private int numberOfDays;

    @Enumerated(EnumType.STRING)
    private TripStatus tripStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    @ToString.Exclude

    private List<Activity> activityList = new ArrayList<>();
}