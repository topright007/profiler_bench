package com.profiler.loadgen.util;

import com.profiler.loadgen.model.LoadTestStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class MetricsCollector {
    
    private final List<Long> latencies = Collections.synchronizedList(new ArrayList<>());
    private long successCount = 0;
    private long failureCount = 0;
    private long startTime = 0;
    
    public synchronized void reset() {
        latencies.clear();
        successCount = 0;
        failureCount = 0;
        startTime = System.currentTimeMillis();
    }
    
    public synchronized void recordSuccess(long latencyMs) {
        latencies.add(latencyMs);
        successCount++;
    }
    
    public synchronized void recordFailure() {
        failureCount++;
    }
    
    public synchronized LoadTestStats getStats() {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        if (latencies.isEmpty()) {
            return LoadTestStats.builder()
                    .totalRequests(0L)
                    .successfulRequests(0L)
                    .failedRequests(failureCount)
                    .averageLatencyMs(0.0)
                    .minLatencyMs(0L)
                    .maxLatencyMs(0L)
                    .requestsPerSecond(0.0)
                    .durationMs(duration)
                    .build();
        }
        
        double averageLatency = latencies.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
        
        long minLatency = latencies.stream()
                .mapToLong(Long::longValue)
                .min()
                .orElse(0L);
        
        long maxLatency = latencies.stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
        
        double rps = duration > 0 ? (successCount * 1000.0) / duration : 0.0;
        
        return LoadTestStats.builder()
                .totalRequests(successCount + failureCount)
                .successfulRequests(successCount)
                .failedRequests(failureCount)
                .averageLatencyMs(averageLatency)
                .minLatencyMs(minLatency)
                .maxLatencyMs(maxLatency)
                .requestsPerSecond(rps)
                .durationMs(duration)
                .build();
    }
}
