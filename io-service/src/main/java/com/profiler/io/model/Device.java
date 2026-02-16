package com.profiler.io.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "devices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(name = "device_type", nullable = false, length = 50)
    private String deviceType; // SENSOR, ACTUATOR, CONTROLLER, CAMERA, etc.
    
    @Column(length = 50)
    private String manufacturer;
    
    @Column(name = "model_number", length = 50)
    private String modelNumber;
    
    @Column(name = "power_consumption")
    private Integer powerConsumption; // in watts
    
    @Column(name = "installation_date")
    private LocalDateTime installationDate;
    
    @Column(name = "status", length = 20)
    private String status; // ACTIVE, INACTIVE, MAINTENANCE
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;
}
