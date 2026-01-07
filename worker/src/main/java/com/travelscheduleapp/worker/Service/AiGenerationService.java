package com.travelscheduleapp.worker.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class AiGenerationService {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    public AiGenerationService(ChatModel chatModel, ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
    }

    public List<AiActivityDto> generateItinerary(String destination, int numberOfDays) {
        int totalActivities = numberOfDays * 3;

        System.out.println(" [AI Service] Asking Gemini for " + totalActivities + " activities in " + destination);

        String message = """
            You are a travel assistant. Generate a travel itinerary for {destination} for {days} days.
            Total activities required: {totalActivities} (Create exactly 3 activities per day).
            
            RULES:
            1. Return ONLY a valid JSON Array.
            2. Strict JSON format. No markdown, no 'Here is your plan' text.
            3. Each activity must have: 'description', 'day', 'duration'.
            
            REQUIRED JSON STRUCTURE (Example):
            [
              \\{"description": "Visit City Center", "day": 1, "duration": "2 hours"\\},
              \\{"description": "Local Museum", "day": 1, "duration": "1.5 hours"\\}
            ]
            """;

        PromptTemplate template = new PromptTemplate(message);
        Prompt prompt = template.create(Map.of(
                "destination", destination,
                "days", numberOfDays,
                "totalActivities", totalActivities
        ));

        try {
            String response = chatModel.call(prompt).getResult().getOutput().getText();

            String cleanJson = cleanGeminiResponse(response);
            return objectMapper.readValue(cleanJson, new TypeReference<>() {});

        } catch (Exception e) {
            System.err.println("AI Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private String cleanGeminiResponse(String response) {
        if (response.contains("```json")) {
            return response.replace("```json", "").replace("```", "").trim();
        } else if (response.contains("```")) {
            return response.replace("```", "").trim();
        }
        return response.trim();
    }
}