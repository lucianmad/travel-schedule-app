package com.travelscheduleapp.worker.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AiGenerationService {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    public AiGenerationService(ChatModel chatModel, ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
    }

    public List<AiActivityDto> generateItinerary(String destination, int numberOfDays, String budget) {
        int totalActivities = numberOfDays * 3;

        System.out.println(" [AI Service] Asking OLLAMA for " + totalActivities + " activities in " + destination);

        String prompt = String.format("""
            You are a travel assistant. Generate a travel itinerary for %s for %d days.
            Total activities required: %d (3 per day).
            
            RULES:
            1. Return ONLY a valid JSON Array.
            2. Use curly braces { } for each activity object.
            3. Do not use markdown or extra text.
            
            REQUIRED JSON FORMAT:
            [
              {
                "description": "Visit the City Center",
                "day": 1,
                "duration": "2 hours"
              },
              {
                "description": "Dinner at local restaurant",
                "day": 2,
                "duration": "1.5 hours"
              }
            ]
            """, destination, numberOfDays, totalActivities);

        try {
            String response = chatModel.call(prompt);

            String cleanJson = response.replace("```json", "").replace("```", "").trim();
            int start = cleanJson.indexOf("[");
            int end = cleanJson.lastIndexOf("]");

            if (start != -1 && end != -1) {
                cleanJson = cleanJson.substring(start, end + 1);
            } else {
                System.err.println(" [AI Service] Invalid JSON format received.");
                return Collections.emptyList();
            }

            return objectMapper.readValue(cleanJson, new TypeReference<List<AiActivityDto>>() {});

        } catch (Exception e) {
            System.err.println("AI Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}