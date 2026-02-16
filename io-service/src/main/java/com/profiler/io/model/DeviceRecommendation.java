package com.profiler.io.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRecommendation {
    private String deviceType;
    private String manufacturer;
    private String modelNumber;
    private Double score;
    private String reason;
    private Integer estimatedCost;
}
