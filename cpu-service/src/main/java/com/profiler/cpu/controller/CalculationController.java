package com.profiler.cpu.controller;

import com.profiler.cpu.model.RecommendationRequest;
import com.profiler.cpu.model.RecommendationResponse;
import com.profiler.cpu.service.RecommendationCalculatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Level 1: REST Controller
 * Entry point for calculation requests
 */
@RestController
@RequestMapping("/api")
public class CalculationController {
    
    private static final Logger logger = LoggerFactory.getLogger(CalculationController.class);
    
    private final RecommendationCalculatorService calculatorService;
    
    public CalculationController(RecommendationCalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }
    
    @PostMapping("/calculate")
    public ResponseEntity<RecommendationResponse> calculate(@RequestBody RecommendationRequest request) {
        logger.info("Received calculation request for customer {}", request.getCustomerId());
        
        RecommendationResponse response = calculatorService.calculate(request);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("CPU Service is healthy");
    }
}
