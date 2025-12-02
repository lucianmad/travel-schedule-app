package com.travelscheduleapp.api.controller;

import com.travelscheduleapp.api.dto.ActivityRequest;
import com.travelscheduleapp.api.dto.ActivityResponse;
import com.travelscheduleapp.api.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
@Tag(name = "Activities")
public class ActivityController {
    private final ActivityService activityService;

    @GetMapping("/{id}")
    @Operation(summary = "Get an activity by id")
    @ApiResponse(responseCode = "200", description = "Activity successfully retrieved")
    @ApiResponse(responseCode = "404", description = "Activity not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    public ResponseEntity<ActivityResponse> getById(@PathVariable Long id,
                                                    @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(activityService.getActivityById(id, userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an activity")
    @ApiResponse(responseCode = "200", description = "Activity successfully updated")
    @ApiResponse(responseCode = "404", description = "Activity not found")
    @ApiResponse(responseCode = "409", description = "Validation failed")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @ApiResponse(responseCode = "400", description = "Invalid number of days")
    public ResponseEntity<ActivityResponse> update(@PathVariable Long id,
                                                   @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
                                                   @Valid @RequestBody ActivityRequest activityRequest) {
        return ResponseEntity.ok(activityService.updateActivity(id, userId, activityRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an activity")
    @ApiResponse(responseCode = "204", description = "Activity successfully deleted")
    @ApiResponse(responseCode = "404", description = "Activity not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId) {
        activityService.deleteActivity(id, userId);
        return ResponseEntity.noContent().build();
    }
}
