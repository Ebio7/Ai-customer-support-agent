package com.hiver.supportagent.controller;

import com.hiver.supportagent.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/evaluation")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class EvaluationController {
    
    private final EvaluationService evaluationService;
    
    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }
    
    @GetMapping("/golden-set")
    public ResponseEntity<Map<String, Object>> evaluateGoldenSet() {
        Map<String, Object> results = evaluationService.evaluateAgainstGoldenSet();
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/baselines")
    public ResponseEntity<Map<String, Object>> compareBaselines() {
        Map<String, Object> results = evaluationService.compareBaselines();
        return ResponseEntity.ok(results);
    }
}