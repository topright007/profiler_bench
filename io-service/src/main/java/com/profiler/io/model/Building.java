package com.profiler.io.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "buildings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Building {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 200)
    private String address;
    
    @Column(name = "building_type", length = 50)
    private String buildingType; // RESIDENTIAL, COMMERCIAL, INDUSTRIAL
    
    @Column(name = "square_meters")
    private Integer squareMeters;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @OneToMany(mappedBy = "building", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Device> devices = new ArrayList<>();
}
