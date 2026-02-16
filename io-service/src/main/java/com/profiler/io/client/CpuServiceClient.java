package com.profiler.io.client;

import com.profiler.io.model.RecommendationRequest;
import com.profiler.io.model.RecommendationResponse;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CpuServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(CpuServiceClient.class);
    
    private final RestTemplate restTemplate;
    private final String cpuServiceUrl;
    
    public CpuServiceClient(RestTemplate restTemplate,
                            @Value("${cpu-service.url}") String cpuServiceUrl) {
        this.restTemplate = restTemplate;
        this.cpuServiceUrl = cpuServiceUrl;
    }
    
    @WithSpan("CpuServiceClient.calculateRecommendations")
    public RecommendationResponse calculateRecommendations(RecommendationRequest request) {
        logger.info("Calling CPU service for customer {} with {} devices", 
                    request.getCustomerId(), 
                    request.getDevices() != null ? request.getDevices().size() : 0);
        
        String url = cpuServiceUrl + "/api/calculate";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<RecommendationRequest> entity = new HttpEntity<>(request, headers);
        
        long startTime = System.currentTimeMillis();
        ResponseEntity<RecommendationResponse> response = restTemplate.postForEntity(
                url, 
                entity, 
                RecommendationResponse.class
        );
        long duration = System.currentTimeMillis() - startTime;
        
        logger.info("CPU service responded in {} ms", duration);
        
        return response.getBody();
    }
}
