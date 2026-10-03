package com.anomaly.backend.dto;

import com.anomaly.backend.model.AnomalyEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyEventDto {

    private Long id;
    private UUID eventId;
    private String sourceId;
    private String metricType;
    private Double value;
    private Double anomalyScore;
    private Double zScore;
    private String severity;
    private Double confidence;
    private String modelUsed;
    private LocalDateTime timestamp;
    private LocalDateTime detectedAt;
    private LocalDateTime createdAt;

    public static AnomalyEventDto fromEntity(AnomalyEvent entity) {
        AnomalyEventDto dto = new AnomalyEventDto();
        dto.setId(entity.getId());
        dto.setEventId(entity.getEventId());
        dto.setSourceId(entity.getSourceId());
        dto.setMetricType(entity.getMetricType());
        dto.setValue(entity.getValue());
        dto.setAnomalyScore(entity.getAnomalyScore());
        dto.setZScore(entity.getZScore());
        dto.setSeverity(entity.getSeverity());
        dto.setConfidence(entity.getConfidence());
        dto.setModelUsed(entity.getModelUsed());
        dto.setTimestamp(entity.getTimestamp());
        dto.setDetectedAt(entity.getDetectedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
