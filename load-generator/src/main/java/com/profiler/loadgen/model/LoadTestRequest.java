package com.profiler.loadgen.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoadTestRequest {
    private List<Long> customerIds;
    private Integer parallelRequests;
    private Integer warmupSeconds;
    private Integer pauseSeconds;
    private Integer measurementSeconds;
}
