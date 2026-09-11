package com.hiver.supportagent.controller;

import com.hiver.supportagent.dto.SupportRequest;
import com.hiver.supportagent.dto.SupportResponse;
import com.hiver.supportagent.entity.Conversation;
import com.hiver.supportagent.service.SupportAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class SupportAgentController {
    
    private final SupportAgentService supportAgentService;
    
    public SupportAgentController(SupportAgentService supportAgentService) {
        this.supportAgentService = supportAgentService;
    }
    
    @PostMapping("/process")
    public ResponseEntity<SupportResponse> processMessage(@RequestBody SupportRequest request) {
        SupportResponse response = supportAgentService.processCustomerMessage(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/conversations")
    public ResponseEntity<List<Conversation>> getAllConversations() {
        List<Conversation> conversations = supportAgentService.getAllConversations();
        return ResponseEntity.ok(conversations);
    }
    
    @GetMapping("/golden-set")
    public ResponseEntity<List<Conversation>> getGoldenSet() {
        List<Conversation> goldenSet = supportAgentService.getGoldenSet();
        return ResponseEntity.ok(goldenSet);
    }
    
    @GetMapping("/intents")
    public ResponseEntity<List<String>> getIntents() {
        List<String> intents = supportAgentService.getAvailableIntents();
        return ResponseEntity.ok(intents);
    }
    
    @PostMapping("/golden-set/{id}")
    public ResponseEntity<Conversation> addToGoldenSet(@PathVariable Long id, @RequestBody String humanLabel) {
        // This would be implemented to mark a conversation as part of the golden set
        // For now, return a placeholder
        return ResponseEntity.ok().build();
    }
}