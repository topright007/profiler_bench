package com.profiler.cpu.service;

import com.profiler.cpu.model.*;
import com.profiler.cpu.util.BlackholeSink;
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
    private final BlackholeSink blackhole;

    @Value("${calculation.min-duration-ms:50}")
    private int minDurationMs;

    @Value("${calculation.max-duration-ms:500}")
    private int maxDurationMs;

    @Value("${calculation.iterations-per-device:1000}")
    private int iterationsPerDevice;

    public RecommendationCalculatorService(DeviceScorerService deviceScorerService,
                                          MathUtils mathUtils,
                                          BlackholeSink blackhole) {
        this.deviceScorerService = deviceScorerService;
        this.mathUtils = mathUtils;
        this.blackhole = blackhole;
    }

    @WithSpan("RecommendationCalculatorService.calculate")
    public RecommendationResponse calculate(RecommendationRequest request) {
        logger.info("Starting calculation for customer {} with {} devices",
                request.getCustomerId(),
                request.getDevices() != null ? request.getDevices().size() : 0);

        long startTime = System.currentTimeMillis();

        performCpuIntensiveWork(request);

        List<DeviceRecommendation> allRecommendations = deviceScorerService.scoreDevices(
                request.getDevices(),
                request.getBuildings(),
                request.getCustomer().getCustomerType()
        );

        List<DeviceRecommendation> topRecommendations = allRecommendations.stream()
                .sorted(Comparator.comparingDouble(DeviceRecommendation::getScore).reversed())
                .limit(10)
                .collect(Collectors.toList());

        long calculationTime = System.currentTimeMillis() - startTime;

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

    private static final int LOAD_MS_PER_DEVICE_FIXED = 30;
    private static final int LOAD_MS_PER_DEVICE_DEFAULT = 100;
    private static final int TIMER_CHECK_EVERY_DEVICES = 10;

    @WithSpan("RecommendationCalculatorService.performCpuIntensiveWork")
    private void performCpuIntensiveWork(RecommendationRequest request) {
        int deviceCount = request.getDevices() != null ? request.getDevices().size() : 1;
        int durationPerDeviceMs = Boolean.TRUE.equals(request.getFixed())
                ? LOAD_MS_PER_DEVICE_FIXED
                : LOAD_MS_PER_DEVICE_DEFAULT;

        for (int d = 0; d < deviceCount; d += TIMER_CHECK_EVERY_DEVICES) {
            int batchSize = Math.min(TIMER_CHECK_EVERY_DEVICES, deviceCount - d);
            long batchDurationMs = (long) batchSize * durationPerDeviceMs;
            processDeviceBatch(batchDurationMs);
        }

        if (request.getCustomer() != null) {
            mathUtils.calculateHash(request.getCustomer().getName(), iterationsPerDevice);
        }
    }

    private void processDeviceBatch(long batchDurationMs) {
        long batchEnd = System.currentTimeMillis() + batchDurationMs;
        int loopCount = 0;

        // Loop 1: main timing loop
        while (System.currentTimeMillis() < batchEnd) {
            long fib = mathUtils.fibonacci(15);
            boolean prime = mathUtils.isPrime(10001);
            blackhole.consume(fib);
            blackhole.consume(prime);

            // 5 if-else branches
            if (loopCount % 5 == 0) {
                long a = mathUtils.fibonacci(12);
                blackhole.consume(a);
            } else if (loopCount % 5 == 1) {
                boolean b = mathUtils.isPrime(1000 + loopCount);
                blackhole.consume(b);
            } else if (loopCount % 5 == 2) {
                long h = mathUtils.calculateHash("batch", 20);
                blackhole.consume(h);
            } else if (loopCount % 5 == 3) {
                double[][] m1 = new double[][]{{1, 0}, {0, 1}};
                double[][] m2 = new double[][]{{1, 1}, {1, 1}};
                double[][] mr = mathUtils.multiplyMatrices(m1, m2);
                blackhole.consume(mr);
            } else {
                long c = mathUtils.fibonacci(10) + mathUtils.calculateHash("x", 5);
                blackhole.consume(c);
            }

            // Loop 2: for
            for (int i = 0; i < 3; i++) {
                long x = mathUtils.fibonacci(10 + i);
                blackhole.consume(x);
            }
            // Loop 3: while
            int j = 0;
            while (j < 2) {
                boolean w = mathUtils.isPrime(1000 + j);
                blackhole.consume(w);
                j++;
            }
            // Loop 4: do-while
            int k = 0;
            do {
                long h = mathUtils.calculateHash("sink", 2);
                blackhole.consume(h);
                k++;
            } while (k < 2);
            // Loop 5: enhanced for
            for (int n : new int[]{1, 2, 3}) {
                double d = n * 1.5 + mathUtils.fibonacci(8);
                blackhole.consume(d);
            }

            loopCount++;
            if (logger.isTraceEnabled()) {
                logger.trace("Processed device batch iteration {}", loopCount);
            }
        }
        blackhole.consume((long) loopCount);
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
