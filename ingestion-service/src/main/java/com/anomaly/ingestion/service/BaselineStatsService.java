package com.anomaly.ingestion.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class BaselineStatsService {

    private final ConcurrentHashMap<String, RunningStats> statsMap = new ConcurrentHashMap<>();

    /**
     * Updates the running statistics for a given source+metric key and returns
     * the current mean and standard deviation using Welford's online algorithm.
     *
     * @param sourceId   the source identifier
     * @param metricType the metric type
     * @param value      the new observed value
     * @return a BaselineResult containing the current mean and standard deviation
     */
    public BaselineResult updateAndGet(String sourceId, String metricType, double value) {
        String key = sourceId + ":" + metricType;
        RunningStats stats = statsMap.computeIfAbsent(key, k -> new RunningStats());

        synchronized (stats) {
            stats.update(value);
            return new BaselineResult(stats.getMean(), stats.getStddev());
        }
    }

    /**
     * Result record containing baseline mean and standard deviation.
     */
    public record BaselineResult(double mean, double stddev) {
    }

    /**
     * Internal class implementing Welford's online algorithm for computing
     * running mean and standard deviation in a single pass.
     */
    private static class RunningStats {
        private long count = 0;
        private double mean = 0.0;
        private double m2 = 0.0;

        void update(double value) {
            count++;
            double delta = value - mean;
            mean += delta / count;
            double delta2 = value - mean;
            m2 += delta * delta2;
        }

        double getMean() {
            return mean;
        }

        double getStddev() {
            if (count < 2) {
                return 0.0;
            }
            return Math.sqrt(m2 / (count - 1));
        }
    }
}
