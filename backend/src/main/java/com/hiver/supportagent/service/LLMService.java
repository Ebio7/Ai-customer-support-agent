package com.hiver.supportagent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LLMService {
    
    private static final Logger log = LoggerFactory.getLogger(LLMService.class);
    
    @Value("${llm.api.key}")
    private String apiKey;
    
    @Value("${llm.api.url}")
    private String apiUrl;
    
    @Value("${llm.api.model}")
    private String model;
    
    @Value("${app.intents}")
    private String availableIntents;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public LLMService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    public String classifyIntent(String customerMessage) {
        String prompt = buildIntentClassificationPrompt(customerMessage);
        return callLLM(prompt);
    }
    
    public String draftReply(String customerMessage, String intent, List<String> similarResponses) {
        String prompt = buildReplyDraftingPrompt(customerMessage, intent, similarResponses);
        return callLLM(prompt);
    }
    
    public String decideEscalation(String customerMessage, String intent, String draftedReply) {
        String prompt = buildEscalationPrompt(customerMessage, intent, draftedReply);
        return callLLM(prompt);
    }
    
    private String buildIntentClassificationPrompt(String customerMessage) {
        String[] intents = availableIntents.split(",");
        StringBuilder intentList = new StringBuilder();
        for (String intent : intents) {
            intentList.append("- ").append(intent.trim()).append("\n");
        }
        
        return String.format("""
            You are a customer support intent classifier. Classify the following customer message into ONE of these intents:
            
            %s
            
            Customer message: "%s"
            
            Respond with ONLY the intent name, nothing else.
            """, intentList.toString(), customerMessage);
    }
    
    private String buildReplyDraftingPrompt(String customerMessage, String intent, List<String> similarResponses) {
        StringBuilder similarResponsesText = new StringBuilder();
        if (similarResponses != null && !similarResponses.isEmpty()) {
            similarResponsesText.append("Here are some similar historical responses for reference:\n");
            for (int i = 0; i < Math.min(3, similarResponses.size()); i++) {
                similarResponsesText.append(i + 1).append(". ").append(similarResponses.get(i)).append("\n");
            }
        }
        
        return String.format("""
            You are a customer support agent for a brand. Draft a helpful, professional response to the customer.
            
            Customer message: "%s"
            Intent: %s
            
            %s
            
            Guidelines:
            - Be empathetic and professional
            - Address the customer's concern directly
            - If it's a technical issue, provide troubleshooting steps
            - If it's a billing question, be clear about amounts and timelines
            - If it's a shipping delay, provide realistic expectations
            - Keep the response concise but helpful
            - Match the tone of similar historical responses
            
            Draft the response:
            """, customerMessage, intent, similarResponsesText.toString());
    }
    
    private String buildEscalationPrompt(String customerMessage, String intent, String draftedReply) {
        return String.format("""
            You are a customer support escalation decision system. Decide whether this case should be AUTO_HANDLED or ESCALATED to a human.
            
            Customer message: "%s"
            Intent: %s
            Drafted reply: "%s"
            
            Respond in this exact JSON format:
            {
              "decision": "AUTO_HANDLE" or "ESCALATE",
              "reason": "brief explanation of why this decision was made",
              "confidence": 0.0 to 1.0
            }
            
            Escalation criteria:
            - ESCALATE if: contains threats, legal issues, severe harassment, complex technical issues beyond standard troubleshooting, requests for management, or any safety concerns
            - AUTO_HANDLE if: routine inquiries, standard troubleshooting, billing questions, shipping delays, or general information requests
            """, customerMessage, intent, draftedReply);
    }

    public String callLLM(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Gemini authentication
            headers.set("x-goog-api-key", apiKey);

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "parts", List.of(
                                            Map.of("text", prompt)
                                    )
                            )
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.3,
                            "maxOutputTokens", 500
                    )
            );

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null) {

                JsonNode root = objectMapper.readTree(response.getBody());

                JsonNode candidates = root.path("candidates");

                if (candidates.isArray() && candidates.size() > 0) {
                    JsonNode parts = candidates
                            .get(0)
                            .path("content")
                            .path("parts");

                    if (parts.isArray() && parts.size() > 0) {
                        String text = parts
                                .get(0)
                                .path("text")
                                .asText();

                        if (text != null && !text.isBlank()) {
                            return text.trim();
                        }
                    }
                }

                log.error("Gemini returned no usable text: {}", response.getBody());
                return "ERROR";
            }

            log.error("Gemini API call failed: {}", response.getStatusCode());
            return "ERROR";

        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            log.error(
                    "Gemini API error: status={}, body={}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );
            return "ERROR";

        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            return "ERROR";
        }
    }
}