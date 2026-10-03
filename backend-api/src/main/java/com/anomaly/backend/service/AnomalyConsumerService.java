package com.anomaly.backend.service;

import com.anomaly.backend.model.AnomalyEvent;
import com.anomaly.backend.repository.AnomalyEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnomalyConsumerService {

    private final AnomalyEventRepository anomalyEventRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "anomaly-events", groupId = "backend-group")
    public void consumeAnomalyEvent(String message) {
        try {
            JsonNode json = objectMapper.readTree(message);

            AnomalyEvent event = new AnomalyEvent();
            event.setEventId(UUID.fromString(json.get("event_id").asText()));
            event.setSourceId(json.get("source_id").asText());
            event.setMetricType(json.get("metric_type").asText());
            event.setValue(json.get("value").asDouble());
            event.setSeverity(json.get("severity").asText());

            if (json.has("anomaly_score") && !json.get("anomaly_score").isNull()) {
                event.setAnomalyScore(json.get("anomaly_score").asDouble());
            }
            if (json.has("z_score") && !json.get("z_score").isNull()) {
                event.setZScore(json.get("z_score").asDouble());
            }
            if (json.has("confidence") && !json.get("confidence").isNull()) {
                event.setConfidence(json.get("confidence").asDouble());
            }
            if (json.has("model_used") && !json.get("model_used").isNull()) {
                event.setModelUsed(json.get("model_used").asText());
            }

            event.setTimestamp(parseDateTime(json.get("timestamp").asText()));
            event.setDetectedAt(parseDateTime(json.get("detected_at").asText()));

            anomalyEventRepository.save(event);
            log.info("Saved anomaly event: {} severity={}", event.getEventId(), event.getSeverity());

        } catch (Exception e) {
            log.error("Failed to process anomaly event message: {}", message, e);
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
}
