package com.profiler.loadgen.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoadTestStats {
    private Long totalRequests;
    private Long successfulRequests;
    private Long failedRequests;
    private Double averageLatencyMs;
    private Long minLatencyMs;
    private Long maxLatencyMs;
    private Double requestsPerSecond;
    private Long durationMs;
}
