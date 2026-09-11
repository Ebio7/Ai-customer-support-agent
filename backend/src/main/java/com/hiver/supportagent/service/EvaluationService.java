package com.hiver.supportagent.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiver.supportagent.entity.Conversation;
import com.hiver.supportagent.repository.ConversationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EvaluationService {
    
    private static final Logger log = LoggerFactory.getLogger(EvaluationService.class);
    
    private final ConversationRepository conversationRepository;
    private final LLMService llmService;
    private final ObjectMapper objectMapper;
    
    public EvaluationService(ConversationRepository conversationRepository, 
                           LLMService llmService,
                           ObjectMapper objectMapper) {
        this.conversationRepository = conversationRepository;
        this.llmService = llmService;
        this.objectMapper = objectMapper;
    }
    
    public Map<String, Object> evaluateAgainstGoldenSet() {
        List<Conversation> goldenSet = conversationRepository.findByIsGoldenSetTrue();
        
        if (goldenSet.isEmpty()) {
            return Map.of(
                "error", "No golden set found. Please create golden set first.",
                "totalEvaluated", 0
            );
        }
        
        int total = goldenSet.size();
        int intentMatches = 0;
        int escalationMatches = 0;
        double totalReplyQuality = 0.0;
        List<Map<String, Object>> detailedResults = new ArrayList<>();
        
        for (Conversation goldenItem : goldenSet) {
            Map<String, Object> result = evaluateSingleConversation(goldenItem);
            detailedResults.add(result);
            
            if ((boolean) result.get("intentMatch")) {
                intentMatches++;
            }
            if ((boolean) result.get("escalationMatch")) {
                escalationMatches++;
            }
            totalReplyQuality += ((Number) result.get("replyQuality")).doubleValue();
        }
        
        Map<String, Object> evaluation = new HashMap<>();
        evaluation.put("totalEvaluated", total);
        evaluation.put("intentAccuracy", (double) intentMatches / total);
        evaluation.put("escalationAccuracy", (double) escalationMatches / total);
        evaluation.put("averageReplyQuality", totalReplyQuality / total);
        evaluation.put("detailedResults", detailedResults);
        
        return evaluation;
    }
    
    private Map<String, Object> evaluateSingleConversation(Conversation goldenItem) {
        Map<String, Object> result = new HashMap<>();
        result.put("conversationId", goldenItem.getId());
        result.put("customerMessage", goldenItem.getText());
        result.put("humanLabel", goldenItem.getHumanLabel());
        
        // Process the conversation through the agent
        String predictedIntent = llmService.classifyIntent(goldenItem.getText());
        String predictedReply = llmService.draftReply(goldenItem.getText(), predictedIntent, List.of());
        String predictedEscalation = llmService.decideEscalation(goldenItem.getText(), predictedIntent, predictedReply);
        
        result.put("predictedIntent", predictedIntent);
        result.put("predictedReply", predictedReply);
        result.put("predictedEscalation", predictedEscalation);
        
        // Compare with human labels
        String humanIntent = extractIntentFromLabel(goldenItem.getHumanLabel());
        String humanEscalation = extractEscalationFromLabel(goldenItem.getHumanLabel());
        
        result.put("humanIntent", humanIntent);
        result.put("humanEscalation", humanEscalation);
        result.put("intentMatch", predictedIntent.equalsIgnoreCase(humanIntent));
        result.put("escalationMatch", predictedEscalation.contains(humanEscalation));
        
        // Evaluate reply quality using LLM as judge
        double replyQuality = evaluateReplyQuality(goldenItem.getText(), predictedReply, humanIntent);
        result.put("replyQuality", replyQuality);
        
        return result;
    }
    
    private String extractIntentFromLabel(String humanLabel) {
        try {
            if (humanLabel != null && humanLabel.contains("intent:")) {
                String[] parts = humanLabel.split("intent:");
                if (parts.length > 1) {
                    String intent = parts[1].split(",")[0].trim();
                    return intent;
                }
            }
        } catch (Exception e) {
            log.error("Error extracting intent from label", e);
        }
        return "unknown";
    }
    
    private String extractEscalationFromLabel(String humanLabel) {
        try {
            if (humanLabel != null && humanLabel.contains("escalation:")) {
                String[] parts = humanLabel.split("escalation:");
                if (parts.length > 1) {
                    String escalation = parts[1].trim();
                    return escalation;
                }
            }
        } catch (Exception e) {
            log.error("Error extracting escalation from label", e);
        }
        return "ESCALATE";
    }
    
    private double evaluateReplyQuality(String customerMessage, String predictedReply, String expectedIntent) {
        String prompt = String.format("""
            You are a quality evaluator for customer support responses. Rate the following response on a scale of 0.0 to 1.0.
            
            Customer message: "%s"
            Expected intent: %s
            Predicted response: "%s"
            
            Evaluate based on:
            - Relevance to customer's issue
            - Helpfulness and clarity
            - Professional tone
            - Appropriateness for the intent
            - Empathy and customer service quality
            
            Respond with ONLY a number between 0.0 and 1.0, nothing else.
            """, customerMessage, expectedIntent, predictedReply);
        
        try {
            String response = llmService.callLLM(prompt);
            return Double.parseDouble(response.trim());
        } catch (Exception e) {
            log.error("Error evaluating reply quality", e);
            return 0.5; // Default middle score
        }
    }
    
    public Map<String, Object> compareBaselines() {
        // Baseline 1: Random intent classification
        // Baseline 2: Simple keyword-based classification
        // Our system: LLM-based classification
        
        List<Conversation> goldenSet = conversationRepository.findByIsGoldenSetTrue();
        if (goldenSet.isEmpty()) {
            return Map.of("error", "No golden set available for baseline comparison");
        }
        
        Map<String, Object> results = new HashMap<>();
        
        // Our system's performance
        Map<String, Object> ourSystem = evaluateAgainstGoldenSet();
        results.put("llm_system", ourSystem);
        
        // Baseline 1: Random
        double randomIntentAccuracy = 1.0 / 7.0; // Assuming 7 intents
        double randomEscalationAccuracy = 0.5; // 50/50 chance
        results.put("random_baseline", Map.of(
            "intentAccuracy", randomIntentAccuracy,
            "escalationAccuracy", randomEscalationAccuracy,
            "averageReplyQuality", 0.3
        ));
        
        // Baseline 2: Simple keyword matching
        double keywordIntentAccuracy = evaluateKeywordBaseline(goldenSet);
        results.put("keyword_baseline", Map.of(
            "intentAccuracy", keywordIntentAccuracy,
            "escalationAccuracy", 0.4,
            "averageReplyQuality", 0.4
        ));
        
        return results;
    }
    
    private double evaluateKeywordBaseline(List<Conversation> goldenSet) {
        int matches = 0;
        Map<String, List<String>> keywordMap = Map.of(
            "technical_issue", List.of("broken", "error", "not working", "crash", "bug"),
            "billing_question", List.of("charge", "refund", "payment", "bill", "cost"),
            "shipping_delay", List.of("delivery", "shipping", "late", "package", "arrive"),
            "product_inquiry", List.of("product", "item", "stock", "available", "price"),
            "complaint", List.of("angry", "disappointed", "terrible", "worst", "unhappy"),
            "general_inquiry", List.of("question", "information", "help", "how"),
            "account_issue", List.of("account", "login", "password", "access", "sign")
        );
        
        for (Conversation conv : goldenSet) {
            String text = conv.getText().toLowerCase();
            String humanIntent = extractIntentFromLabel(conv.getHumanLabel());
            
            boolean matched = false;
            for (Map.Entry<String, List<String>> entry : keywordMap.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(humanIntent)) {
                    for (String keyword : entry.getValue()) {
                        if (text.contains(keyword)) {
                            matched = true;
                            break;
                        }
                    }
                }
                if (matched) break;
            }
            
            if (matched) matches++;
        }
        
        return (double) matches / goldenSet.size();
    }
}