package com.anomaly.ingestion.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnrichedMetricEvent {

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("source_id")
    private String sourceId;

    @JsonProperty("metric_type")
    private String metricType;

    @JsonProperty("value")
    private Double value;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("metadata")
    private RawMetricEvent.Metadata metadata;

    @JsonProperty("z_score")
    private Double zScore;

    @JsonProperty("baseline_mean")
    private Double baselineMean;

    @JsonProperty("baseline_stddev")
    private Double baselineStddev;

    @JsonProperty("time_bucket")
    private Instant timeBucket;

    @JsonProperty("is_duplicate")
    private Boolean isDuplicate;
}
