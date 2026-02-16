package com.profiler.loadgen.service;

import com.profiler.loadgen.model.RecommendationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IoServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(IoServiceClient.class);
    
    private final RestTemplate restTemplate;
    private final String ioServiceUrl;
    
    public IoServiceClient(RestTemplate restTemplate,
                          @Value("${io-service.url}") String ioServiceUrl) {
        this.restTemplate = restTemplate;
        this.ioServiceUrl = ioServiceUrl;
    }
    
    public RecommendationResponse getRecommendations(Long customerId) {
        String url = ioServiceUrl + "/api/recommendations/" + customerId;
        
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<RecommendationResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                RecommendationResponse.class
        );
        
        return response.getBody();
    }
}
