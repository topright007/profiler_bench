package com.profiler.io.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StatisticsCalculator {
    
    public Map<String, Long> calculateDeviceTypeCounts(List<String> deviceTypes) {
        return deviceTypes.stream()
                .collect(Collectors.groupingBy(
                        type -> type,
                        Collectors.counting()
                ));
    }
    
    public Double calculateAveragePowerConsumption(List<Integer> powerConsumptions) {
        if (powerConsumptions.isEmpty()) {
            return 0.0;
        }
        return powerConsumptions.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }
    
    public Integer calculateTotalPowerConsumption(List<Integer> powerConsumptions) {
        return powerConsumptions.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }
    
    public Map<String, Integer> calculateBuildingTypeDistribution(List<String> buildingTypes) {
        return buildingTypes.stream()
                .collect(Collectors.groupingBy(
                        type -> type,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }
}
