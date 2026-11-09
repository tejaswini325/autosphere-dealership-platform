package com.autosphere.service;

import com.autosphere.model.Lead;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class AiDraftService {
    @Value("${app.groq-api-key:}")
    String apiKey;

    @Value("${app.groq-model:llama-3.3-70b-versatile}")
    String model;

    public String providerName() {
        return apiKey == null || apiKey.isBlank() ? "fallback" : "groq";
    }

    public String draft(Lead lead) {
        String vehicle = lead.vehicleModel == null ? "our dealership" : lead.vehicleModel;
        String fallback = "Hi " + lead.name + ", thank you for your interest in " + vehicle
                + ". I would be happy to understand what matters most to you and help with the next steps."
                + " Would a quick call or a showroom visit work better? Regards, AutoSphere Sales Team";

        if (apiKey == null || apiKey.isBlank()) {
            return fallback;
        }

        try {
            RestClient client = RestClient.create();
            Map<String, Object> body = Map.of(
                    "model", model,
                    "temperature", 0.3,
                    "messages", List.of(
                            Map.of("role", "system", "content",
                                    "Write a concise, courteous first-reply draft for a car dealership enquiry. "
                                    + "Do not invent prices, discounts, stock status, or promises. "
                                    + "The message is a draft for a human to review."),
                            Map.of("role", "user", "content",
                                    "Customer: " + lead.name + "; vehicle: " + lead.vehicleModel
                                    + "; message: " + lead.message)
                    )
            );

            Map<?, ?> result = client.post()
                    .uri("https://api.groq.com/openai/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            if (result == null || !(result.get("choices") instanceof List<?> choices) || choices.isEmpty()) {
                return fallback;
            }
            Object firstChoice = choices.get(0);
            if (!(firstChoice instanceof Map<?, ?> choice)
                    || !(choice.get("message") instanceof Map<?, ?> message)) {
                return fallback;
            }
            Object content = message.get("content");
            return content == null ? fallback : String.valueOf(content);
        } catch (Exception exception) {
            return fallback + "\n\n[Fallback draft: AI provider was unavailable. Please review before use.]";
        }
    }

    public String summary(Lead lead) {
        String fallback = "Buyer: " + lead.name + ". Interest: "
                + (lead.vehicleModel == null ? "General enquiry" : lead.vehicleModel)
                + ". Request: " + (lead.message == null ? "No message supplied." : lead.message)
                + ". Showroom: " + (lead.showroom == null ? "Lucknow" : lead.showroom) + ".";
        if (apiKey == null || apiKey.isBlank()) return fallback;
        try {
            RestClient client = RestClient.create();
            Map<String,Object> body = Map.of(
                "model", model, "temperature", 0.2,
                "messages", List.of(
                    Map.of("role","system","content","Summarise a car dealership buyer enquiry in at most three concise factual bullet points. Do not invent budget, urgency, or preferences."),
                    Map.of("role","user","content","Customer: "+lead.name+"; vehicle: "+lead.vehicleModel+"; showroom: "+lead.showroom+"; enquiry: "+lead.message)
                )
            );
            Map<?,?> result=client.post().uri("https://api.groq.com/openai/v1/chat/completions")
                .header("Authorization","Bearer "+apiKey).contentType(MediaType.APPLICATION_JSON).body(body).retrieve().body(Map.class);
            if(result!=null && result.get("choices") instanceof List<?> choices && !choices.isEmpty()
                && choices.get(0) instanceof Map<?,?> choice && choice.get("message") instanceof Map<?,?> message
                && message.get("content")!=null) return String.valueOf(message.get("content"));
        } catch(Exception ignored) { }
        return fallback;
    }
}
