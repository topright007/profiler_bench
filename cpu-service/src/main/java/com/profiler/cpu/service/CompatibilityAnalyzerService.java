package com.profiler.cpu.service;

import com.profiler.cpu.model.BuildingContext;
import com.profiler.cpu.model.DeviceContext;
import com.profiler.cpu.util.MathUtils;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Level 4: Compatibility Analyzer
 * Analyzes device compatibility with building requirements
 */
@Service
public class CompatibilityAnalyzerService {
    
    private static final Logger logger = LoggerFactory.getLogger(CompatibilityAnalyzerService.class);
    
    private final ScoreAggregatorService scoreAggregatorService;
    private final MathUtils mathUtils;
    
    public CompatibilityAnalyzerService(ScoreAggregatorService scoreAggregatorService,
                                       MathUtils mathUtils) {
        this.scoreAggregatorService = scoreAggregatorService;
        this.mathUtils = mathUtils;
    }
    
    @WithSpan("CompatibilityAnalyzerService.analyzeCompatibility")
    public Map<String, Double> analyzeCompatibility(List<DeviceContext> devices,
                                                    List<BuildingContext> buildings,
                                                    @SpanAttribute("customerType") String customerType) {
        logger.debug("Analyzing compatibility for {} devices across {} buildings",
                    devices.size(), buildings.size());
        
        // Group devices by type
        Map<String, List<DeviceContext>> devicesByType = devices.stream()
                .collect(Collectors.groupingBy(DeviceContext::getDeviceType));
        
        Map<String, Double> compatibilityScores = new java.util.HashMap<>();
        
        for (Map.Entry<String, List<DeviceContext>> entry : devicesByType.entrySet()) {
            String deviceType = entry.getKey();
            List<DeviceContext> typeDevices = entry.getValue();
            
            // CPU work: Calculate fibonacci for device count
            int fibResult = (int) mathUtils.fibonacci(Math.min(20, typeDevices.size()));
            
            // Calculate individual scores
            List<Double> scores = new ArrayList<>();
            for (DeviceContext device : typeDevices) {
                double score = calculateDeviceScore(device, buildings, customerType);
                scores.add(score);
            }
            
            // CPU work: Prime number check
            boolean isPrime = mathUtils.isPrime(typeDevices.size());
            double primeBonus = isPrime ? 1.1 : 1.0;
            
            // Aggregate scores (calls level 5)
            double aggregatedScore = scoreAggregatorService.aggregateScores(
                    scores,
                    deviceType,
                    typeDevices.size()
            );
            
            compatibilityScores.put(deviceType, aggregatedScore * primeBonus);
        }
        
        logger.debug("Calculated compatibility scores for {} device types", compatibilityScores.size());
        return compatibilityScores;
    }
    
    private double calculateDeviceScore(DeviceContext device, List<BuildingContext> buildings, String customerType) {
        double score = 50.0; // Base score
        
        // Factor in power consumption
        if (device.getPowerConsumption() != null) {
            score += (100 - device.getPowerConsumption()) / 10.0;
        }
        
        // Factor in building compatibility
        for (BuildingContext building : buildings) {
            if (building.getSquareMeters() != null) {
                score += building.getSquareMeters() / 100.0;
            }
        }
        
        // Customer type multiplier
        if ("LARGE".equals(customerType)) {
            score *= 1.2;
        }
        
        return Math.min(100.0, score);
    }
}
