package com.profiler.io.service;

import com.profiler.io.client.CpuServiceClient;
import com.profiler.io.model.RecommendationRequest;
import com.profiler.io.model.RecommendationResponse;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Level 2: Recommendation Service
 * Main business logic orchestration
 */
@Service
public class RecommendationService {
    
    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);
    
    private final CustomerEnrichmentService customerEnrichmentService;
    private final CpuServiceClient cpuServiceClient;
    
    public RecommendationService(CustomerEnrichmentService customerEnrichmentService,
                                 CpuServiceClient cpuServiceClient) {
        this.customerEnrichmentService = customerEnrichmentService;
        this.cpuServiceClient = cpuServiceClient;
    }
    
    @WithSpan("getRecommendations")
    @Transactional(readOnly = true)
    public RecommendationResponse getRecommendations(@SpanAttribute("customerId") Long customerId) {
        logger.info("Processing recommendation request for customer {}", customerId);
        
        long startTime = System.currentTimeMillis();
        
        // Enrich customer data (calls level 3, which calls 4 and 5)
        RecommendationRequest request = customerEnrichmentService.enrichCustomerData(customerId);
        
        long enrichmentTime = System.currentTimeMillis() - startTime;
        logger.info("Data enrichment completed in {} ms", enrichmentTime);
        
        // Call CPU service for calculation
        RecommendationResponse response = cpuServiceClient.calculateRecommendations(request);
        
        long totalTime = System.currentTimeMillis() - startTime;
        logger.info("Total recommendation processing time: {} ms", totalTime);
        
        return response;
    }
}
