package com.profiler.io.service;

import com.profiler.io.model.Device;
import com.profiler.io.model.DeviceContext;
import com.profiler.io.util.DataMapper;
import com.profiler.io.util.StatisticsCalculator;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Level 4: Device Aggregation Service
 * Aggregates and processes device data
 */
@Service
public class DeviceAggregationService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceAggregationService.class);
    
    private final BuildingAnalysisService buildingAnalysisService;
    private final DataMapper dataMapper;
    private final StatisticsCalculator statisticsCalculator;
    
    public DeviceAggregationService(BuildingAnalysisService buildingAnalysisService,
                                    DataMapper dataMapper,
                                    StatisticsCalculator statisticsCalculator) {
        this.buildingAnalysisService = buildingAnalysisService;
        this.dataMapper = dataMapper;
        this.statisticsCalculator = statisticsCalculator;
    }
    
    @WithSpan("aggregateDevices")
    public List<DeviceContext> aggregateDevices(@SpanAttribute("customerId") Long customerId) {
        logger.debug("Aggregating devices for customer {}", customerId);
        
        List<Device> devices = buildingAnalysisService.getAllDevicesForCustomer(customerId);
        
        // Calculate statistics (adds depth to call stack)
        List<String> deviceTypes = devices.stream()
                .map(Device::getDeviceType)
                .collect(Collectors.toList());
        
        Map<String, Long> typeCounts = statisticsCalculator.calculateDeviceTypeCounts(deviceTypes);
        logger.debug("Device type distribution: {}", typeCounts);
        
        List<Integer> powerConsumptions = devices.stream()
                .map(Device::getPowerConsumption)
                .filter(p -> p != null)
                .collect(Collectors.toList());
        
        Double avgPower = statisticsCalculator.calculateAveragePowerConsumption(powerConsumptions);
        logger.debug("Average power consumption: {} watts", avgPower);
        
        // Convert to contexts
        List<DeviceContext> contexts = dataMapper.toDeviceContextList(devices);
        logger.debug("Aggregated {} device contexts", contexts.size());
        
        return contexts;
    }
}
