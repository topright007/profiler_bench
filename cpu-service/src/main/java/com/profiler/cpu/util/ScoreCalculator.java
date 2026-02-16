package com.profiler.cpu.util;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Score calculation utilities
 */
@Component
public class ScoreCalculator {
    
    public double calculateWeightedScore(List<Double> scores, List<Double> weights) {
        if (scores.size() != weights.size()) {
            throw new IllegalArgumentException("Scores and weights must have same size");
        }
        
        double totalWeight = weights.stream().mapToDouble(Double::doubleValue).sum();
        double weightedSum = 0.0;
        
        for (int i = 0; i < scores.size(); i++) {
            weightedSum += scores.get(i) * weights.get(i);
        }
        
        return weightedSum / totalWeight;
    }
    
    public double normalizeScore(double score, double min, double max) {
        if (max == min) return 0.5;
        return (score - min) / (max - min);
    }
    
    public double[] calculateStatistics(List<Double> values) {
        double[] stats = new double[4]; // mean, median, stddev, variance
        
        // Mean
        stats[0] = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        
        // Median
        double[] sorted = values.stream().mapToDouble(Double::doubleValue).sorted().toArray();
        int mid = sorted.length / 2;
        stats[1] = sorted.length % 2 == 0 ? (sorted[mid - 1] + sorted[mid]) / 2 : sorted[mid];
        
        // Variance and StdDev
        double variance = 0.0;
        for (double value : values) {
            variance += Math.pow(value - stats[0], 2);
        }
        stats[3] = variance / values.size();
        stats[2] = Math.sqrt(stats[3]);
        
        return stats;
    }
}
