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
}
