package com.profiler.cpu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class CpuServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CpuServiceApplication.class, args);
    }
}
