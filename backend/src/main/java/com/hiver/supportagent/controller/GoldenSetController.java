package com.hiver.supportagent.controller;

import com.hiver.supportagent.service.GoldenSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/golden-set")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class GoldenSetController {
    
    private final GoldenSetService goldenSetService;
    
    public GoldenSetController(GoldenSetService goldenSetService) {
        this.goldenSetService = goldenSetService;
    }
    
    @PostMapping("/load")
    public ResponseEntity<Map<String, Object>> loadGoldenSet(@RequestBody Map<String, String> request) {
        String jsonFilePath = request.get("jsonFilePath");
        String message = goldenSetService.loadGoldenSetFromJson(jsonFilePath);
        
        return ResponseEntity.ok(Map.of(
            "message", message,
            "count", goldenSetService.getGoldenSetCount()
        ));
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearGoldenSet() {
        String message = goldenSetService.clearGoldenSet();
        return ResponseEntity.ok(Map.of("message", message));
    }
    
    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getGoldenSetCount() {
        return ResponseEntity.ok(Map.of("count", goldenSetService.getGoldenSetCount()));
    }
}