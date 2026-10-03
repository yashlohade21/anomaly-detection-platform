package com.anomaly.ingestion.service;

import com.anomaly.ingestion.model.RawMetricEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SchemaValidationService {

    /**
     * Validates that the required fields of a RawMetricEvent are present.
     *
     * @param event the raw metric event to validate
     * @return true if valid, false otherwise
     */
    public boolean isValid(RawMetricEvent event) {
        if (event == null) {
            log.warn("Received null metric event, skipping");
            return false;
        }

        if (event.getEventId() == null || event.getEventId().isBlank()) {
            log.warn("Invalid metric event: event_id is null or blank");
            return false;
        }

        if (event.getSourceId() == null || event.getSourceId().isBlank()) {
            log.warn("Invalid metric event: source_id is null or blank for event_id={}", event.getEventId());
            return false;
        }

        if (event.getMetricType() == null || event.getMetricType().isBlank()) {
            log.warn("Invalid metric event: metric_type is null or blank for event_id={}", event.getEventId());
            return false;
        }

        if (event.getValue() == null) {
            log.warn("Invalid metric event: value is null for event_id={}", event.getEventId());
            return false;
        }

        return true;
    }
}
