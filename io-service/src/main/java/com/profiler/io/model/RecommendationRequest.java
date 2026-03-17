package com.profiler.io.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {
    private Long customerId;
    private List<DeviceContext> devices;
    private List<BuildingContext> buildings;
    private CustomerContext customer;
    /** If true, CPU service uses optimized 30ms load per device; otherwise 100ms. */
    private Boolean fixed;
}
