package com.hiver.supportagent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiver.supportagent.entity.Conversation;
import com.hiver.supportagent.repository.ConversationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoldenSetService {
    
    private static final Logger log = LoggerFactory.getLogger(GoldenSetService.class);
    
    @Value("${app.brand.name}")
    private String brandName;
    
    private final ConversationRepository conversationRepository;
    private final ObjectMapper objectMapper;
    
    public GoldenSetService(ConversationRepository conversationRepository, ObjectMapper objectMapper) {
        this.conversationRepository = conversationRepository;
        this.objectMapper = objectMapper;
    }

    public String loadGoldenSetFromJson(String jsonFilePath) {
        try {
            log.info("Loading golden set from classpath: {}", jsonFilePath);

            String resourcePath = jsonFilePath;

            // Remove leading slash if supplied
            if (resourcePath.startsWith("/")) {
                resourcePath = resourcePath.substring(1);
            }

            InputStream inputStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream(resourcePath);

            if (inputStream == null) {
                throw new IOException(
                        "Golden set file not found on classpath: " + resourcePath
                );
            }

            JsonNode root = objectMapper.readTree(inputStream);
            JsonNode examples = root.path("examples");

            if (!examples.isArray()) {
                throw new IOException("JSON does not contain an 'examples' array");
            }

            List<Conversation> goldenConversations = new ArrayList<>();
            int loadedCount = 0;

            for (JsonNode example : examples) {
                try {
                    Conversation conversation = new Conversation();

                    conversation.setTweetId(
                            "golden_" + example.path("id").asText()
                    );

                    conversation.setAuthorId(
                            "golden_user_" + example.path("id").asText()
                    );

                    conversation.setAuthorName("Golden Set User");

                    conversation.setText(
                            example.path("customer_message").asText()
                    );

                    conversation.setBrand(brandName);
                    conversation.setInboundOrOutbound("inbound");

                    conversation.setResponseTweetId("golden_response_" + example.path("id").asText());

                    conversation.setIntent(
                            example.path("intent").asText()
                    );

                    conversation.setEscalationDecision(
                            example.path("escalation_decision").asText()
                    );

                    conversation.setHumanLabel(
                            example.path("human_label").asText()
                    );

                    conversation.setIsGoldenSet(true);
                    conversation.setConfidenceScore(1.0);


                    goldenConversations.add(conversation);
                    loadedCount++;

                } catch (Exception e) {
                    log.warn(
                            "Error processing golden set example: {}",
                            e.getMessage()
                    );
                }
            }

            conversationRepository.saveAll(goldenConversations);

            String message = String.format(
                    "Successfully loaded %d golden set examples",
                    loadedCount
            );

            log.info(message);
            return message;

        } catch (IOException e) {
            log.error("Error loading golden set from JSON", e);
            throw new RuntimeException(
                    "Failed to load golden set: " + e.getMessage(),
                    e
            );
        }
    }
    
    public int getGoldenSetCount() {
        return conversationRepository.findByIsGoldenSetTrue().size();
    }
    
    public String clearGoldenSet() {
        List<Conversation> goldenSet = conversationRepository.findByIsGoldenSetTrue();
        conversationRepository.deleteAll(goldenSet);
        return String.format("Cleared %d golden set examples", goldenSet.size());
    }
}