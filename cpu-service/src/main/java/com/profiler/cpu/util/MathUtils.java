package com.profiler.cpu.util;

import org.springframework.stereotype.Component;

/**
 * CPU-intensive mathematical operations
 */
@Component
public class MathUtils {
    
    /**
     * Perform matrix multiplication for CPU load
     */
    public double[][] multiplyMatrices(double[][] a, double[][] b) {
        int n = a.length;
        double[][] result = new double[n][n];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        
        return result;
    }
    
    /**
     * Calculate fibonacci numbers recursively
     */
    public long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
    
    /**
     * Prime number check
     */
    public boolean isPrime(long n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        
        for (long i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Calculate hash using custom algorithm
     */
    public long calculateHash(String input, int iterations) {
        long hash = 0;
        for (int iter = 0; iter < iterations; iter++) {
            for (char c : input.toCharArray()) {
                hash = ((hash << 5) - hash) + c;
                hash = hash & hash; // Convert to 32bit integer
            }
        }
        return hash;
    }
}
