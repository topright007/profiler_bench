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
public class RecommendationResponse {
    private Long customerId;
    private List<DeviceRecommendation> recommendations;
    private String calculationMethod;
    private Long calculationTimeMs;
    private Integer totalDevicesAnalyzed;
}
