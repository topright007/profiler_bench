package com.profiler.io.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceContext {
    private Long id;
    private String deviceType;
    private String manufacturer;
    private String modelNumber;
    private Integer powerConsumption;
    private String status;
}
