package com.profiler.io.repository;

import com.profiler.io.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    
    @Query("SELECT b FROM Building b WHERE b.customer.id = :customerId")
    List<Building> findByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT b FROM Building b LEFT JOIN FETCH b.devices WHERE b.customer.id = :customerId")
    List<Building> findByCustomerIdWithDevices(@Param("customerId") Long customerId);
}
