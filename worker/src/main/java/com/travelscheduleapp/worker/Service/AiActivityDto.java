package com.travelscheduleapp.worker.Service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AiActivityDto {
    @JsonProperty("description")
    private String description;

    @JsonProperty("day")
    private int day;

    @JsonProperty("duration")
    private String duration;
}