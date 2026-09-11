package com.hiver.supportagent.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiver.supportagent.dto.SupportRequest;
import com.hiver.supportagent.dto.SupportResponse;
import com.hiver.supportagent.entity.Conversation;
import com.hiver.supportagent.repository.ConversationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SupportAgentService {
    
    private static final Logger log = LoggerFactory.getLogger(SupportAgentService.class);
    
    @Value("${app.brand.name}")
    private String brandName;
    
    @Value("${app.auto-handle.threshold}")
    private double autoHandleThreshold;
    
    private final LLMService llmService;
    private final ConversationRepository conversationRepository;
    private final ObjectMapper objectMapper;
    
    public SupportAgentService(LLMService llmService, 
                               ConversationRepository conversationRepository,
                               ObjectMapper objectMapper) {
        this.llmService = llmService;
        this.conversationRepository = conversationRepository;
        this.objectMapper = objectMapper;
    }
    
    public SupportResponse processCustomerMessage(SupportRequest request) {
        log.info("Processing customer message from: {}", request.getCustomerName());
        
        // Step 1: Classify intent
        String intent = llmService.classifyIntent(request.getCustomerMessage());
        log.info("Classified intent: {}", intent);
        
        // Step 2: Find similar historical responses
        List<String> similarResponses = findSimilarResponses(intent);
        
        // Step 3: Draft reply
        String draftedReply = llmService.draftReply(request.getCustomerMessage(), intent, similarResponses);
        log.info("Drafted reply: {}", draftedReply);
        
        // Step 4: Decide escalation
        String escalationDecision = llmService.decideEscalation(request.getCustomerMessage(), intent, draftedReply);
        Map<String, Object> escalationResult = parseEscalationDecision(escalationDecision);
        
        String decision = (String) escalationResult.getOrDefault("decision", "ESCALATE");
        String reason = (String) escalationResult.getOrDefault("reason", "Unable to determine");
        double confidence = ((Number) escalationResult.getOrDefault("confidence", 0.5)).doubleValue();
        
        // Step 5: Save conversation to database
        Conversation conversation = saveConversation(request, intent, draftedReply, decision, reason, confidence, similarResponses);
        
        // Step 6: Build response
        SupportResponse response = new SupportResponse();
        response.setIntent(intent);
        response.setDraftedReply(draftedReply);
        response.setEscalationDecision(decision);
        response.setEscalationReason(reason);
        response.setConfidenceScore(confidence);
        response.setSimilarResponses(similarResponses.toArray(new String[0]));
        response.setRequestId(request.getTweetId());
        response.setConversationId(conversation.getId());
        
        return response;
    }
    
    private List<String> findSimilarResponses(String intent) {
        try {
            List<Conversation> similarConversations = conversationRepository.findSimilarResponses(brandName, intent);
            return similarConversations.stream()
                    .limit(5)
                    .map(Conversation::getResponseText)
                    .filter(text -> text != null && !text.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding similar responses", e);
            return List.of();
        }
    }
    
    private Map<String, Object> parseEscalationDecision(String escalationDecision) {
        try {
            // Try to parse as JSON
            if (escalationDecision.startsWith("{")) {
                return objectMapper.readValue(escalationDecision, new TypeReference<Map<String, Object>>() {});
            }
            
            // Fallback: parse simple text response
            Map<String, Object> result = new HashMap<>();
            String upperDecision = escalationDecision.toUpperCase();
            
            if (upperDecision.contains("AUTO_HANDLE") || upperDecision.contains("AUTO-HANDLE")) {
                result.put("decision", "AUTO_HANDLE");
                result.put("reason", "Standard case suitable for automated handling");
                result.put("confidence", 0.8);
            } else {
                result.put("decision", "ESCALATE");
                result.put("reason", "Case requires human intervention");
                result.put("confidence", 0.7);
            }
            
            return result;
        } catch (Exception e) {
            log.error("Error parsing escalation decision", e);
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("decision", "ESCALATE");
            fallback.put("reason", "Error in decision process - defaulting to human review");
            fallback.put("confidence", 0.0);
            return fallback;
        }
    }
    
    private Conversation saveConversation(SupportRequest request, String intent, String draftedReply, 
                                        String decision, String reason, double confidence, 
                                        List<String> similarResponses) {
        Conversation conversation = new Conversation();
        conversation.setTweetId(request.getTweetId());
        conversation.setAuthorId(request.getCustomerId());
        conversation.setAuthorName(request.getCustomerName());
        conversation.setText(request.getCustomerMessage());
        conversation.setBrand(brandName);
        conversation.setInboundOrOutbound("inbound");
        conversation.setResponseTweetId("draft_" + System.currentTimeMillis());
        conversation.setIntent(intent);
        conversation.setDraftedReply(draftedReply);
        conversation.setEscalationDecision(decision);
        conversation.setEscalationReason(reason);
        conversation.setConfidenceScore(confidence);
        
        try {
            conversation.setSimilarResponses(objectMapper.writeValueAsString(similarResponses));
        } catch (Exception e) {
            log.error("Error serializing similar responses", e);
            conversation.setSimilarResponses("[]");
        }
        
        return conversationRepository.save(conversation);
    }
    
    public List<Conversation> getGoldenSet() {
        return conversationRepository.findByIsGoldenSetTrue();
    }
    
    public List<Conversation> getAllConversations() {
        return conversationRepository.findByBrand(brandName);
    }
    
    public List<String> getAvailableIntents() {
        return conversationRepository.findDistinctIntents();
    }
}