package com.profiler.io.controller;

import com.profiler.io.model.RecommendationResponse;
import com.profiler.io.service.RecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Level 1: REST Controller
 * Entry point for recommendation requests
 */
@RestController
@RequestMapping("/api")
public class RecommendationController {
    
    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);
    
    private final RecommendationService recommendationService;
    
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }
    
    @PostMapping("/recommendations/{customerId}")
    public ResponseEntity<RecommendationResponse> getRecommendations(@PathVariable Long customerId) {
        logger.info("Received recommendation request for customer {}", customerId);
        
        try {
            RecommendationResponse response = recommendationService.getRecommendations(customerId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error processing recommendation for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("IO Service is healthy");
    }
}
