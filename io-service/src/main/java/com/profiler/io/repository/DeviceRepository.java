package com.profiler.io.repository;

import com.profiler.io.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    @Query("SELECT d FROM Device d WHERE d.building.id = :buildingId")
    List<Device> findByBuildingId(@Param("buildingId") Long buildingId);
    
    @Query("SELECT d FROM Device d WHERE d.building.customer.id = :customerId")
    List<Device> findByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT COUNT(d) FROM Device d WHERE d.building.customer.id = :customerId")
    Long countByCustomerId(@Param("customerId") Long customerId);
}
