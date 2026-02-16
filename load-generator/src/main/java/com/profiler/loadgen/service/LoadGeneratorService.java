package com.profiler.loadgen.service;

import com.profiler.loadgen.model.LoadTestRequest;
import com.profiler.loadgen.model.LoadTestResponse;
import com.profiler.loadgen.model.LoadTestStats;
import com.profiler.loadgen.util.MetricsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

@Service
public class LoadGeneratorService {
    
    private static final Logger logger = LoggerFactory.getLogger(LoadGeneratorService.class);
    
    private final IoServiceClient ioServiceClient;
    private final Random random = new Random();
    
    public LoadGeneratorService(IoServiceClient ioServiceClient) {
        this.ioServiceClient = ioServiceClient;
    }
    
    public LoadTestResponse runLoadTest(LoadTestRequest request) {
        logger.info("Starting load test: {} parallel requests, warmup: {}s, pause: {}s, measurement: {}s",
                   request.getParallelRequests(),
                   request.getWarmupSeconds(),
                   request.getPauseSeconds(),
                   request.getMeasurementSeconds());
        
        long totalStartTime = System.currentTimeMillis();
        
        // Validate request
        if (request.getCustomerIds() == null || request.getCustomerIds().isEmpty()) {
            return LoadTestResponse.builder()
                    .status("FAILED")
                    .message("Customer IDs list cannot be empty")
                    .build();
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(request.getParallelRequests());
        
        try {
            // Phase 1: Warmup (10% of requested load)
            logger.info("Phase 1: Warmup phase starting");
            MetricsCollector warmupMetrics = new MetricsCollector();
            int warmupThreads = Math.max(1, request.getParallelRequests() / 10);
            runLoadPhase(executor, request.getCustomerIds(), warmupThreads, 
                        request.getWarmupSeconds(), warmupMetrics);
            LoadTestStats warmupStats = warmupMetrics.getStats();
            logger.info("Warmup completed: {} requests, avg latency: {} ms",
                       warmupStats.getSuccessfulRequests(),
                       warmupStats.getAverageLatencyMs());
            
            // Phase 2: Pause
            if (request.getPauseSeconds() > 0) {
                logger.info("Phase 2: Pause phase for {} seconds", request.getPauseSeconds());
                Thread.sleep(request.getPauseSeconds() * 1000L);
            }
            
            // Phase 3: Measurement (full load)
            logger.info("Phase 3: Measurement phase starting with {} threads", request.getParallelRequests());
            MetricsCollector measurementMetrics = new MetricsCollector();
            runLoadPhase(executor, request.getCustomerIds(), request.getParallelRequests(),
                        request.getMeasurementSeconds(), measurementMetrics);
            LoadTestStats measurementStats = measurementMetrics.getStats();
            logger.info("Measurement completed: {} requests, avg latency: {} ms, RPS: {}",
                       measurementStats.getSuccessfulRequests(),
                       measurementStats.getAverageLatencyMs(),
                       measurementStats.getRequestsPerSecond());
            
            long totalDuration = System.currentTimeMillis() - totalStartTime;
            
            return LoadTestResponse.builder()
                    .status("COMPLETED")
                    .phase("FINISHED")
                    .warmupStats(warmupStats)
                    .measurementStats(measurementStats)
                    .totalDurationMs(totalDuration)
                    .message("Load test completed successfully")
                    .build();
            
        } catch (InterruptedException e) {
            logger.error("Load test interrupted", e);
            Thread.currentThread().interrupt();
            return LoadTestResponse.builder()
                    .status("FAILED")
                    .message("Load test was interrupted: " + e.getMessage())
                    .build();
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private void runLoadPhase(ExecutorService executor, List<Long> customerIds, 
                             int parallelRequests, int durationSeconds,
                             MetricsCollector metrics) throws InterruptedException {
        metrics.reset();
        
        long endTime = System.currentTimeMillis() + (durationSeconds * 1000L);
        CountDownLatch latch = new CountDownLatch(parallelRequests);
        
        // Start worker threads
        for (int i = 0; i < parallelRequests; i++) {
            executor.submit(() -> {
                try {
                    while (System.currentTimeMillis() < endTime) {
                        Long customerId = customerIds.get(random.nextInt(customerIds.size()));
                        
                        long requestStart = System.currentTimeMillis();
                        try {
                            ioServiceClient.getRecommendations(customerId);
                            long latency = System.currentTimeMillis() - requestStart;
                            metrics.recordSuccess(latency);
                        } catch (Exception e) {
                            logger.debug("Request failed for customer {}: {}", customerId, e.getMessage());
                            metrics.recordFailure();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all threads to complete
        latch.await();
    }
}
