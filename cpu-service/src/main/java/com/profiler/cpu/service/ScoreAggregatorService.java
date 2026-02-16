package com.profiler.cpu.service;

import com.profiler.cpu.util.MathUtils;
import com.profiler.cpu.util.ScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Level 5: Score Aggregator
 * Aggregates individual scores using CPU-intensive operations
 */
@Service
public class ScoreAggregatorService {
    
    private static final Logger logger = LoggerFactory.getLogger(ScoreAggregatorService.class);
    
    private final MathUtils mathUtils;
    private final ScoreCalculator scoreCalculator;
    
    public ScoreAggregatorService(MathUtils mathUtils, ScoreCalculator scoreCalculator) {
        this.mathUtils = mathUtils;
        this.scoreCalculator = scoreCalculator;
    }
    
    public double aggregateScores(List<Double> scores, String deviceType, int deviceCount) {
        logger.debug("Aggregating {} scores for device type {}", scores.size(), deviceType);
        
        // CPU work: Calculate hash for device type
        long hash = mathUtils.calculateHash(deviceType, 1000);
        
        // CPU work: Matrix multiplication
        int matrixSize = Math.min(10, scores.size());
        double[][] matrix1 = createMatrix(matrixSize, hash);
        double[][] matrix2 = createMatrix(matrixSize, hash + 1);
        double[][] result = mathUtils.multiplyMatrices(matrix1, matrix2);
        
        // CPU work: Calculate statistics
        double[] stats = scoreCalculator.calculateStatistics(scores);
        
        // Combine results
        double aggregatedScore = scoreCalculator.calculateWeightedScore(
                scores,
                createWeights(scores.size())
        );
        
        // Normalize based on device count
        double normalizedScore = scoreCalculator.normalizeScore(
                aggregatedScore * deviceCount,
                0,
                100 * deviceCount
        );
        
        logger.debug("Aggregated score: {} (hash: {}, mean: {})", normalizedScore, hash, stats[0]);
        return normalizedScore;
    }
    
    private double[][] createMatrix(int size, long seed) {
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = ((seed + i * size + j) % 100) / 10.0;
            }
        }
        return matrix;
    }
    
    private List<Double> createWeights(int size) {
        List<Double> weights = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            weights.add(1.0 / size);
        }
        return weights;
    }
}
