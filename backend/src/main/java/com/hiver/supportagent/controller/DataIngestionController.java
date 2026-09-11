package com.hiver.supportagent.controller;

import com.hiver.supportagent.service.DataIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class DataIngestionController {
    
    private final DataIngestionService dataIngestionService;
    
    public DataIngestionController(DataIngestionService dataIngestionService) {
        this.dataIngestionService = dataIngestionService;
    }
    
    @PostMapping("/ingest")
    public ResponseEntity<Map<String, Object>> ingestData(@RequestParam("file") MultipartFile file) {
        String message = dataIngestionService.ingestCSVData(file);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("totalConversations", dataIngestionService.getConversationCount());
        response.put("brandConversations", dataIngestionService.getBrandConversationCount());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalConversations", dataIngestionService.getConversationCount());
        stats.put("brandConversations", dataIngestionService.getBrandConversationCount());
        
        return ResponseEntity.ok(stats);
    }
}