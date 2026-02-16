package com.profiler.io.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerContext {
    private Long id;
    private String name;
    private String customerType;
    private Integer totalBuildings;
    private Integer totalDevices;
}
