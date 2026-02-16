package com.profiler.loadgen.controller;

import com.profiler.loadgen.model.LoadTestRequest;
import com.profiler.loadgen.model.LoadTestResponse;
import com.profiler.loadgen.service.LoadGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/load")
public class LoadTestController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoadTestController.class);
    
    private final LoadGeneratorService loadGeneratorService;
    
    public LoadTestController(LoadGeneratorService loadGeneratorService) {
        this.loadGeneratorService = loadGeneratorService;
    }
    
    @PostMapping("/start")
    public ResponseEntity<LoadTestResponse> startLoadTest(@RequestBody LoadTestRequest request) {
        logger.info("Received load test request for {} customers with {} parallel requests",
                   request.getCustomerIds() != null ? request.getCustomerIds().size() : 0,
                   request.getParallelRequests());
        
        // Validate request
        if (request.getParallelRequests() == null || request.getParallelRequests() < 1) {
            return ResponseEntity.badRequest().body(
                    LoadTestResponse.builder()
                            .status("FAILED")
                            .message("parallelRequests must be at least 1")
                            .build()
            );
        }
        
        LoadTestResponse response = loadGeneratorService.runLoadTest(request);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Load Generator is healthy");
    }
}
