package com.profiler.cpu.service;

import com.profiler.cpu.model.*;
import com.profiler.cpu.util.MathUtils;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Level 2: Recommendation Calculator
 * Main calculation orchestration with CPU-intensive work
 */
@Service
public class RecommendationCalculatorService {
    
    private static final Logger logger = LoggerFactory.getLogger(RecommendationCalculatorService.class);
    
    private final DeviceScorerService deviceScorerService;
    private final MathUtils mathUtils;
    
    @Value("${calculation.min-duration-ms:50}")
    private int minDurationMs;
    
    @Value("${calculation.max-duration-ms:500}")
    private int maxDurationMs;
    
    @Value("${calculation.iterations-per-device:1000}")
    private int iterationsPerDevice;
    
    public RecommendationCalculatorService(DeviceScorerService deviceScorerService,
                                          MathUtils mathUtils) {
        this.deviceScorerService = deviceScorerService;
        this.mathUtils = mathUtils;
    }
    
    @WithSpan("RecommendationCalculatorService.calculate")
    public RecommendationResponse calculate(RecommendationRequest request) {
        logger.info("Starting calculation for customer {} with {} devices",
                   request.getCustomerId(),
                   request.getDevices() != null ? request.getDevices().size() : 0);
        
        long startTime = System.currentTimeMillis();
        
        // Ensure minimum CPU time by doing intensive calculations
        performCpuIntensiveWork(request);
        
        // Score devices (calls level 3)
        List<DeviceRecommendation> allRecommendations = deviceScorerService.scoreDevices(
                request.getDevices(),
                request.getBuildings(),
                request.getCustomer().getCustomerType()
        );
        
        // Sort by score and take top 10
        List<DeviceRecommendation> topRecommendations = allRecommendations.stream()
                .sorted(Comparator.comparingDouble(DeviceRecommendation::getScore).reversed())
                .limit(10)
                .collect(Collectors.toList());
        
        long calculationTime = System.currentTimeMillis() - startTime;
        
        // If calculation was too fast, do more work
        if (calculationTime < minDurationMs) {
            performAdditionalCpuWork(minDurationMs - calculationTime);
            calculationTime = System.currentTimeMillis() - startTime;
        }
        
        logger.info("Calculation completed in {} ms", calculationTime);
        
        return RecommendationResponse.builder()
                .customerId(request.getCustomerId())
                .recommendations(topRecommendations)
                .calculationMethod("Advanced ML-based scoring algorithm")
                .calculationTimeMs(calculationTime)
                .totalDevicesAnalyzed(request.getDevices() != null ? request.getDevices().size() : 0)
                .build();
    }
    
    /** Duration per device in ms: 30 when fixed (optimized), 100 otherwise (buggy). */
    private static final int LOAD_MS_PER_DEVICE_FIXED = 30;
    private static final int LOAD_MS_PER_DEVICE_DEFAULT = 100;
    private static final int TIMER_CHECK_EVERY_DEVICES = 10;

    @WithSpan("RecommendationCalculatorService.performCpuIntensiveWork")
    private void performCpuIntensiveWork(RecommendationRequest request) {
        int deviceCount = request.getDevices() != null ? request.getDevices().size() : 1;
        int durationPerDeviceMs = Boolean.TRUE.equals(request.getFixed())
                ? LOAD_MS_PER_DEVICE_FIXED
                : LOAD_MS_PER_DEVICE_DEFAULT;

        // CPU load: busyloop for durationPerDeviceMs per device; use system timer every 10 devices
        for (int d = 0; d < deviceCount; d += TIMER_CHECK_EVERY_DEVICES) {
            int batchSize = Math.min(TIMER_CHECK_EVERY_DEVICES, deviceCount - d);
            long batchDurationMs = (long) batchSize * durationPerDeviceMs;
            processDeviceBatch(batchDurationMs);
        }

        // CPU work: Hash calculations
        if (request.getCustomer() != null) {
            mathUtils.calculateHash(request.getCustomer().getName(), iterationsPerDevice);
        }
    }

    private void processDeviceBatch(long batchDurationMs) {
        long batchEnd = System.currentTimeMillis() + batchDurationMs;
        while (System.currentTimeMillis() < batchEnd) {
            mathUtils.fibonacci(15);
        }
    }
    
    @WithSpan("RecommendationCalculatorService.performAdditionalCpuWork")
    private void performAdditionalCpuWork(@SpanAttribute("additionalMs") long additionalMs) {
        long targetTime = System.currentTimeMillis() + additionalMs;
        int iterations = 0;
        
        while (System.currentTimeMillis() < targetTime) {
            mathUtils.fibonacci(15);
            mathUtils.isPrime(iterations + 1000);
            iterations++;
        }
        
        logger.debug("Performed {} additional iterations to reach target duration", iterations);
    }
}
