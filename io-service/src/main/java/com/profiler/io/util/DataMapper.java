package com.profiler.io.util;

import com.profiler.io.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataMapper {
    
    public DeviceContext toDeviceContext(Device device) {
        return DeviceContext.builder()
                .id(device.getId())
                .deviceType(device.getDeviceType())
                .manufacturer(device.getManufacturer())
                .modelNumber(device.getModelNumber())
                .powerConsumption(device.getPowerConsumption())
                .status(device.getStatus())
                .build();
    }
    
    public BuildingContext toBuildingContext(Building building, int deviceCount) {
        return BuildingContext.builder()
                .id(building.getId())
                .buildingType(building.getBuildingType())
                .squareMeters(building.getSquareMeters())
                .deviceCount(deviceCount)
                .build();
    }
    
    public CustomerContext toCustomerContext(Customer customer, int totalBuildings, int totalDevices) {
        return CustomerContext.builder()
                .id(customer.getId())
                .name(customer.getName())
                .customerType(customer.getCustomerType())
                .totalBuildings(totalBuildings)
                .totalDevices(totalDevices)
                .build();
    }
    
    public List<DeviceContext> toDeviceContextList(List<Device> devices) {
        return devices.stream()
                .map(this::toDeviceContext)
                .collect(Collectors.toList());
    }
}
