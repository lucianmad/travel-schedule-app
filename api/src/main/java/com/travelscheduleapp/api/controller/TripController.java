package com.travelscheduleapp.api.controller;

import com.travelscheduleapp.api.dto.*;
import com.travelscheduleapp.api.service.ActivityService;
import com.travelscheduleapp.api.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@Tag(name = "Trips")
public class TripController {
    private final TripService tripService;
    private final ActivityService activityService;

    @GetMapping
    @Operation(summary = "Retrieve all trips")
    @ApiResponse(responseCode = "200", description = "Trips successfully retrieved", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TripResponse.class))))
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    public ResponseEntity<List<TripResponse>> getAll(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(tripService.getAllTrips(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a trip by id")
    @ApiResponse(responseCode = "200", description = "Trip successfully retrieved", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TripResponse.class))))
    @ApiResponse(responseCode = "404", description = "Trip not found", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    public ResponseEntity<TripResponse> getById(@PathVariable Long id,
                                                @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(tripService.getTripById(id, userId));
    }

    @PostMapping
    @Operation(summary = "Create a trip")
    @ApiResponse(responseCode = "201", description = "Trip successfully created", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TripResponse.class))))
    @ApiResponse(responseCode = "409", description = "Validation failed", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    public ResponseEntity<TripResponse> create(@Valid @RequestBody TripRequest tripRequest,
                                               @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId){
        var response = tripService.createTrip(userId, tripRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a trip")
    @ApiResponse(responseCode = "200", description = "Trip successfully updated", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TripResponse.class))))
    @ApiResponse(responseCode = "404", description = "Trip not found", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    @ApiResponse(responseCode = "409", description = "Validation failed", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = "application/json", array = @ArraySchema(schema =  @Schema(implementation = ErrorResponse.class))))
    public ResponseEntity<TripResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody TripRequest tripRequest,
                                               @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId){
        return ResponseEntity.ok(tripService.updateTrip(id, userId, tripRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a trip")
    @ApiResponse(responseCode = "204", description = "Trip successfully deleted")
    @ApiResponse(responseCode = "404", description = "Trip not found", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId){
        tripService.deleteTrip(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{tripId}/activities")
    @Operation(summary = "Get all the activities for a trip")
    @ApiResponse(responseCode = "200", description = "Activities successfully retrieved", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ActivityResponse.class))))
    @ApiResponse(responseCode = "404", description = "Trip not found", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    public ResponseEntity<List<ActivityResponse>> getTripActivities(@PathVariable Long tripId,
                                                                    @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(activityService.getActivities(tripId, userId));
    }

    @PostMapping("/{tripId}/activities")
    @Operation(summary = "Create and activity for a trip")
    @ApiResponse(responseCode = "201", description = "Activity successfully created", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ActivityResponse.class))))
    @ApiResponse(responseCode = "404", description = "Trip not found", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    @ApiResponse(responseCode = "409", description = "Validation failed", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    public ResponseEntity<ActivityResponse> addActivity(@PathVariable Long tripId,
                                                        @Valid @RequestBody ActivityCreateRequest request,
                                                        @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId) {
        var response = activityService.addActivity(tripId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{tripId}/activities/reorder")
    @Operation(summary = "Reorder activities from a trip")
    @ApiResponse(responseCode = "200", description = "Activities successfully reordered")
    @ApiResponse(responseCode = "404", description = "Trip not found", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    @ApiResponse(responseCode = "409", description = "Validation failed", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    @ApiResponse(responseCode = "400", description = "Bad activity ids", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    public ResponseEntity<Void> reorderActivities(
            @PathVariable Long tripId,
            @Valid @RequestBody ActivityReorderRequest request,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId
    ) {
        activityService.reorderActivities(tripId, userId, request);
        return ResponseEntity.ok().build();
    }
}
