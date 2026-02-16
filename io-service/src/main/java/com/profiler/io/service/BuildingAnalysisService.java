package com.profiler.io.service;

import com.profiler.io.model.*;
import com.profiler.io.repository.BuildingRepository;
import com.profiler.io.repository.DeviceRepository;
import com.profiler.io.util.DataMapper;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Level 5: Building Analysis Service
 * Analyzes building data and creates building contexts
 */
@Service
public class BuildingAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(BuildingAnalysisService.class);
    
    private final BuildingRepository buildingRepository;
    private final DeviceRepository deviceRepository;
    private final DataMapper dataMapper;
    
    public BuildingAnalysisService(BuildingRepository buildingRepository,
                                   DeviceRepository deviceRepository,
                                   DataMapper dataMapper) {
        this.buildingRepository = buildingRepository;
        this.deviceRepository = deviceRepository;
        this.dataMapper = dataMapper;
    }
    
    @WithSpan("BuildingAnalysisService.analyzeBuildingsForCustomer")
    public List<BuildingContext> analyzeBuildingsForCustomer(@SpanAttribute("customerId") Long customerId) {
        logger.debug("Analyzing buildings for customer {}", customerId);
        
        List<Building> buildings = buildingRepository.findByCustomerId(customerId);
        List<BuildingContext> contexts = new ArrayList<>();
        
        for (Building building : buildings) {
            int deviceCount = deviceRepository.findByBuildingId(building.getId()).size();
            BuildingContext context = dataMapper.toBuildingContext(building, deviceCount);
            contexts.add(context);
        }
        
        logger.debug("Analyzed {} buildings for customer {}", contexts.size(), customerId);
        return contexts;
    }
    
    @WithSpan("BuildingAnalysisService.getAllDevicesForCustomer")
    public List<Device> getAllDevicesForCustomer(@SpanAttribute("customerId") Long customerId) {
        logger.debug("Fetching all devices for customer {}", customerId);
        return deviceRepository.findByCustomerId(customerId);
    }
}
