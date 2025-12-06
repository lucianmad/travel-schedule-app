package com.travelscheduleapp.worker.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiGenerationService {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    public AiGenerationService(ChatModel chatModel, ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
    }

    public List<String> generateItinerary(String destination, String budget) {
        System.out.println(" [AI Service] Calling LOCAL OLLAMA for: " + destination);

        String prompt = String.format(
                "You are a travel assistant. Generate 5 tourist activities for %s. " +
                        "RULES: \n" +
                        "1. Return ONLY a raw JSON array of strings.\n" +
                        "2. Do NOT use markdown code blocks (like ```json).\n" +
                        "3. Do NOT add any introductory text.\n" +
                        "4. Keep activity names under 100 characters.\n" +
                        "Example format: [\"Activity 1\", \"Activity 2\"]",
                destination
        );

        try {
            String response = chatModel.call(prompt);
            System.out.println(" [AI Service] Raw response: " + response);

            String cleanJson = response.replace("```json", "").replace("```", "").trim();

            int start = cleanJson.indexOf("[");
            int end = cleanJson.lastIndexOf("]");
            if (start != -1 && end != -1) {
                cleanJson = cleanJson.substring(start, end + 1);
            }

            return objectMapper.readValue(cleanJson, new TypeReference<List<String>>() {});

        } catch (Exception e) {
            System.err.println("AI Error: " + e.getMessage());
            return List.of("Visit " + destination + " Center (Fallback)", "Local Park Walk");
        }
    }
}