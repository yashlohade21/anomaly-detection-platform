import logging
import threading
from collections import deque
from contextlib import asynccontextmanager
from datetime import datetime
from typing import List

from fastapi import FastAPI, HTTPException

from src.config import ML_BUFFER_SIZE, ML_RETRAIN_INTERVAL
from src.detector.isolation_forest import IsolationForestDetector
from src.kafka_consumer import MetricsConsumer
from src.kafka_producer import AnomalyProducer
from src.models import (
    AnomalyEvent,
    DetectRequest,
    DetectResponse,
    EnrichedMetricEvent,
)

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
)
logger = logging.getLogger(__name__)

# ------------------------------------------------------------------
# Shared state
# ------------------------------------------------------------------
detector = IsolationForestDetector()
producer = AnomalyProducer()
consumer = MetricsConsumer()

event_buffer: List[EnrichedMetricEvent] = []
buffer_lock = threading.Lock()
events_since_last_train: int = 0
total_events_processed: int = 0
anomalies_detected: int = 0
retrain_buffer: deque = deque(maxlen=ML_RETRAIN_INTERVAL)


# ------------------------------------------------------------------
# Event handling — runs inside the consumer thread
# ------------------------------------------------------------------
def _handle_event(event: EnrichedMetricEvent) -> None:
    global events_since_last_train, total_events_processed, anomalies_detected

    total_events_processed += 1

    # Phase 1: buffering for initial training
    if not detector.is_trained:
        with buffer_lock:
            event_buffer.append(event)
            if len(event_buffer) >= ML_BUFFER_SIZE:
                logger.info(
                    "Buffer full (%d events). Training IsolationForest...",
                    len(event_buffer),
                )
                detector.train(event_buffer)
                events_since_last_train = 0
        return

    # Phase 2: prediction
    retrain_buffer.append(event)
    events_since_last_train += 1

    try:
        is_anomaly, anomaly_score, confidence = detector.predict(event)
    except RuntimeError:
        return

    if is_anomaly:
        severity = IsolationForestDetector.classify_severity(
            anomaly_score, event.z_score
        )
        anomaly = AnomalyEvent(
            metric_id=event.metric_id,
            service_name=event.service_name,
            metric_name=event.metric_name,
            value=event.value,
            timestamp=event.timestamp,
            anomaly_score=round(anomaly_score, 6),
            z_score=round(event.z_score, 6),
            severity=severity,
            confidence=round(confidence, 6),
            model_used="IsolationForest",
            detected_at=datetime.utcnow().isoformat() + "Z",
        )

        try:
            producer.publish(anomaly)
            anomalies_detected += 1
            logger.info(
                "ANOMALY DETECTED | service=%s metric=%s value=%.4f "
                "score=%.4f z=%.4f severity=%s confidence=%.4f",
                anomaly.service_name,
                anomaly.metric_name,
                anomaly.value,
                anomaly.anomaly_score,
                anomaly.z_score,
                anomaly.severity.value,
                anomaly.confidence,
            )
        except Exception:
            logger.exception("Failed to publish anomaly event")

    # Phase 3: periodic retraining
    if events_since_last_train >= ML_RETRAIN_INTERVAL:
        logger.info(
            "Retrain threshold reached (%d events). Retraining in background...",
            events_since_last_train,
        )
        training_data = list(retrain_buffer)
        events_since_last_train = 0
        t = threading.Thread(target=detector.train, args=(training_data,), daemon=True)
        t.start()


def _consumer_loop() -> None:
    try:
        consumer.start(on_event=_handle_event)
    except Exception:
        logger.exception("Consumer loop crashed")


# ------------------------------------------------------------------
# FastAPI lifespan
# ------------------------------------------------------------------
@asynccontextmanager
async def lifespan(app: FastAPI):
    producer.connect()
    thread = threading.Thread(target=_consumer_loop, daemon=True, name="kafka-consumer")
    thread.start()
    logger.info("ML service started — consumer thread running")
    yield
    consumer.stop()
    producer.close()
    logger.info("ML service shutting down")


app = FastAPI(
    title="Anomaly Detection ML Service",
    version="1.0.0",
    lifespan=lifespan,
)


# ------------------------------------------------------------------
# Endpoints
# ------------------------------------------------------------------
@app.get("/health")
async def health():
    return {
        "status": "healthy",
        "model_trained": detector.is_trained,
        "total_events_processed": total_events_processed,
        "anomalies_detected": anomalies_detected,
        "buffer_size": len(event_buffer) if not detector.is_trained else 0,
        "events_since_last_train": events_since_last_train,
    }


@app.post("/api/v1/detect", response_model=DetectResponse)
async def detect(request: DetectRequest):
    if not detector.is_trained:
        raise HTTPException(
            status_code=503,
            detail=(
                f"Model not trained yet. Buffered {len(event_buffer)}/{ML_BUFFER_SIZE} events."
            ),
        )

    event = EnrichedMetricEvent(
        metric_id=request.metric_id,
        service_name=request.service_name,
        metric_name=request.metric_name,
        value=request.value,
        timestamp=request.timestamp or datetime.utcnow().isoformat() + "Z",
        z_score=request.z_score,
    )

    is_anomaly, anomaly_score, confidence = detector.predict(event)
    severity = IsolationForestDetector.classify_severity(anomaly_score, event.z_score)

    return DetectResponse(
        is_anomaly=is_anomaly,
        anomaly_score=round(anomaly_score, 6),
        confidence=round(confidence, 6),
        severity=severity,
        model_trained=True,
        message="Anomaly detected" if is_anomaly else "Normal",
    )
