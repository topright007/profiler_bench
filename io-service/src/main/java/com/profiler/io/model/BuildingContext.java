package com.profiler.io.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingContext {
    private Long id;
    private String buildingType;
    private Integer squareMeters;
    private Integer deviceCount;
}
