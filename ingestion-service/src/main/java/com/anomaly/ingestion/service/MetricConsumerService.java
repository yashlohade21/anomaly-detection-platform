package com.anomaly.ingestion.service;

import com.anomaly.ingestion.model.EnrichedMetricEvent;
import com.anomaly.ingestion.model.RawMetricEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricConsumerService {

    private static final String ENRICHED_TOPIC = "enriched-metrics";

    private final SchemaValidationService schemaValidationService;
    private final BloomFilterService bloomFilterService;
    private final EnrichmentService enrichmentService;
    private final KafkaTemplate<String, EnrichedMetricEvent> kafkaTemplate;

    @KafkaListener(topics = "raw-metrics", groupId = "ingestion-group")
    public void consume(RawMetricEvent event) {
        log.info("Received raw metric event: event_id={}", event != null ? event.getEventId() : "null");

        if (!schemaValidationService.isValid(event)) {
            log.warn("Dropping invalid event: event_id={}", event != null ? event.getEventId() : "null");
            return;
        }

        boolean isDuplicate = bloomFilterService.isDuplicate(event.getEventId());

        EnrichedMetricEvent enrichedEvent = enrichmentService.enrich(event, isDuplicate);

        kafkaTemplate.send(ENRICHED_TOPIC, enrichedEvent.getSourceId(), enrichedEvent)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish enriched event: event_id={}, error={}",
                                enrichedEvent.getEventId(), ex.getMessage(), ex);
                    } else {
                        log.info("Published enriched event to {}: event_id={}, partition={}, offset={}",
                                ENRICHED_TOPIC,
                                enrichedEvent.getEventId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
