package com.profiler.io.service;

import com.profiler.io.model.*;
import com.profiler.io.repository.CustomerRepository;
import com.profiler.io.util.DataMapper;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Level 3: Customer Enrichment Service
 * Enriches customer data with related entities
 */
@Service
public class CustomerEnrichmentService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerEnrichmentService.class);
    
    private final CustomerRepository customerRepository;
    private final DeviceAggregationService deviceAggregationService;
    private final BuildingAnalysisService buildingAnalysisService;
    private final DataMapper dataMapper;
    
    public CustomerEnrichmentService(CustomerRepository customerRepository,
                                     DeviceAggregationService deviceAggregationService,
                                     BuildingAnalysisService buildingAnalysisService,
                                     DataMapper dataMapper) {
        this.customerRepository = customerRepository;
        this.deviceAggregationService = deviceAggregationService;
        this.buildingAnalysisService = buildingAnalysisService;
        this.dataMapper = dataMapper;
    }
    
    @WithSpan("enrichCustomerData")
    public RecommendationRequest enrichCustomerData(@SpanAttribute("customerId") Long customerId) {
        logger.debug("Enriching data for customer {}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));
        
        // Get aggregated devices (calls level 4)
        List<DeviceContext> deviceContexts = deviceAggregationService.aggregateDevices(customerId);
        
        // Get building analysis (calls level 5)
        List<BuildingContext> buildingContexts = buildingAnalysisService.analyzeBuildingsForCustomer(customerId);
        
        // Create customer context
        CustomerContext customerContext = dataMapper.toCustomerContext(
                customer,
                buildingContexts.size(),
                deviceContexts.size()
        );
        
        RecommendationRequest request = RecommendationRequest.builder()
                .customerId(customerId)
                .customer(customerContext)
                .buildings(buildingContexts)
                .devices(deviceContexts)
                .build();
        
        logger.debug("Enriched customer data: {} buildings, {} devices",
                     buildingContexts.size(), deviceContexts.size());
        
        return request;
    }
}
