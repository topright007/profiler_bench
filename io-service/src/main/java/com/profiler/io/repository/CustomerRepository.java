package com.profiler.io.repository;

import com.profiler.io.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.buildings WHERE c.id = :id")
    Optional<Customer> findByIdWithBuildings(@Param("id") Long id);
    
    Optional<Customer> findByEmail(String email);
}
