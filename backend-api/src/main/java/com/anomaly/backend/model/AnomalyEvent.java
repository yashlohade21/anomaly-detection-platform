package com.anomaly.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "anomaly_events")
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", unique = true, nullable = false)
    private UUID eventId;

    @Column(name = "source_id", nullable = false, length = 100)
    private String sourceId;

    @Column(name = "metric_type", nullable = false, length = 100)
    private String metricType;

    @Column(nullable = false)
    private Double value;

    @Column(name = "anomaly_score")
    private Double anomalyScore;

    @Column(name = "z_score")
    private Double zScore;

    @Column(nullable = false, length = 20)
    private String severity;

    private Double confidence;

    @Column(name = "model_used", length = 50)
    private String modelUsed;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
