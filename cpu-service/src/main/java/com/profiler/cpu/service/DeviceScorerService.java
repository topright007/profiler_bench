package com.profiler.cpu.service;

import com.profiler.cpu.model.BuildingContext;
import com.profiler.cpu.model.DeviceContext;
import com.profiler.cpu.model.DeviceRecommendation;
import com.profiler.cpu.util.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Level 3: Device Scorer
 * Scores potential device recommendations
 */
@Service
public class DeviceScorerService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceScorerService.class);
    
    private final CompatibilityAnalyzerService compatibilityAnalyzerService;
    private final MathUtils mathUtils;
    
    private static final String[] MANUFACTURERS = {"TechCorp", "SmartDevices Inc", "IoT Solutions", "AutomationPro"};
    private static final String[] DEVICE_TYPES = {"SENSOR", "ACTUATOR", "CONTROLLER", "CAMERA", "THERMOSTAT", "SMART_LOCK"};
    
    public DeviceScorerService(CompatibilityAnalyzerService compatibilityAnalyzerService,
                              MathUtils mathUtils) {
        this.compatibilityAnalyzerService = compatibilityAnalyzerService;
        this.mathUtils = mathUtils;
    }
    
    public List<DeviceRecommendation> scoreDevices(List<DeviceContext> existingDevices,
                                                    List<BuildingContext> buildings,
                                                    String customerType) {
        logger.debug("Scoring devices for {} existing devices", existingDevices.size());
        
        // Get compatibility scores (calls level 4)
        Map<String, Double> compatibilityScores = compatibilityAnalyzerService.analyzeCompatibility(
                existingDevices,
                buildings,
                customerType
        );
        
        List<DeviceRecommendation> recommendations = new ArrayList<>();
        
        // Generate recommendations for each device type
        for (String deviceType : DEVICE_TYPES) {
            // CPU work: Calculate hash for device type
            long typeHash = mathUtils.calculateHash(deviceType, 500);
            
            double baseScore = compatibilityScores.getOrDefault(deviceType, 50.0);
            
            // CPU work: Check if score is prime-ish
            boolean scoreIsPrime = mathUtils.isPrime((long) baseScore);
            
            for (String manufacturer : MANUFACTURERS) {
                // CPU work: Calculate hash for manufacturer
                long mfgHash = mathUtils.calculateHash(manufacturer, 500);
                
                // Calculate final score
                double score = calculateFinalScore(baseScore, typeHash, mfgHash, scoreIsPrime);
                
                if (score > 60.0) { // Only recommend if score is good enough
                    DeviceRecommendation recommendation = DeviceRecommendation.builder()
                            .deviceType(deviceType)
                            .manufacturer(manufacturer)
                            .modelNumber("MODEL-" + String.format("%04d", Math.abs((int) (mfgHash % 10000))))
                            .score(score)
                            .reason("Compatible with existing infrastructure")
                            .estimatedCost((int) (500 + (score * 10)))
                            .build();
                    
                    recommendations.add(recommendation);
                }
            }
        }
        
        logger.debug("Generated {} device recommendations", recommendations.size());
        return recommendations;
    }
    
    private double calculateFinalScore(double baseScore, long typeHash, long mfgHash, boolean isPrime) {
        double score = baseScore;
        
        // Factor in hash values
        score += (typeHash % 20) - 10;
        score += (mfgHash % 15) - 7;
        
        // Prime bonus
        if (isPrime) {
            score *= 1.05;
        }
        
        return Math.max(0, Math.min(100, score));
    }
}
