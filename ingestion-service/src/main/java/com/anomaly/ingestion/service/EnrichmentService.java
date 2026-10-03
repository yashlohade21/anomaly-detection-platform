package com.anomaly.ingestion.service;

import com.anomaly.ingestion.model.EnrichedMetricEvent;
import com.anomaly.ingestion.model.RawMetricEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrichmentService {

    private final BaselineStatsService baselineStatsService;

    /**
     * Enriches a raw metric event with baseline statistics (z-score, mean, stddev),
     * a time bucket (truncated to minute), and a duplicate flag.
     *
     * @param rawEvent    the raw metric event
     * @param isDuplicate whether this event was flagged as a duplicate
     * @return the enriched metric event
     */
    public EnrichedMetricEvent enrich(RawMetricEvent rawEvent, boolean isDuplicate) {
        BaselineStatsService.BaselineResult baseline = baselineStatsService.updateAndGet(
                rawEvent.getSourceId(),
                rawEvent.getMetricType(),
                rawEvent.getValue()
        );

        double zScore = calculateZScore(rawEvent.getValue(), baseline.mean(), baseline.stddev());

        Instant timeBucket = rawEvent.getTimestamp() != null
                ? rawEvent.getTimestamp().truncatedTo(ChronoUnit.MINUTES)
                : Instant.now().truncatedTo(ChronoUnit.MINUTES);

        EnrichedMetricEvent enrichedEvent = EnrichedMetricEvent.builder()
                .eventId(rawEvent.getEventId())
                .sourceId(rawEvent.getSourceId())
                .metricType(rawEvent.getMetricType())
                .value(rawEvent.getValue())
                .unit(rawEvent.getUnit())
                .timestamp(rawEvent.getTimestamp())
                .metadata(rawEvent.getMetadata())
                .zScore(zScore)
                .baselineMean(baseline.mean())
                .baselineStddev(baseline.stddev())
                .timeBucket(timeBucket)
                .isDuplicate(isDuplicate)
                .build();

        log.info("Enriched event: event_id={}, source_id={}, metric_type={}, value={}, z_score={}, "
                        + "baseline_mean={}, baseline_stddev={}, is_duplicate={}",
                enrichedEvent.getEventId(),
                enrichedEvent.getSourceId(),
                enrichedEvent.getMetricType(),
                enrichedEvent.getValue(),
                enrichedEvent.getZScore(),
                enrichedEvent.getBaselineMean(),
                enrichedEvent.getBaselineStddev(),
                enrichedEvent.getIsDuplicate());

        return enrichedEvent;
    }

    private double calculateZScore(double value, double mean, double stddev) {
        if (stddev == 0.0) {
            return 0.0;
        }
        return (value - mean) / stddev;
    }
}
