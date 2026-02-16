package com.profiler.loadgen.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoadTestResponse {
    private String status;
    private String phase;
    private LoadTestStats warmupStats;
    private LoadTestStats measurementStats;
    private Long totalDurationMs;
    private String message;
}
