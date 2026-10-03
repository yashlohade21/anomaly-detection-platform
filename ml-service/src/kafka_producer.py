import json
import logging
from typing import Optional

from confluent_kafka import Producer

from src.config import KAFKA_BROKER, TOPIC_ANOMALY_EVENTS
from src.models import AnomalyEvent

logger = logging.getLogger(__name__)


class AnomalyProducer:
    def __init__(
        self,
        broker: str = KAFKA_BROKER,
        topic: str = TOPIC_ANOMALY_EVENTS,
    ):
        self._conf = {
            "bootstrap.servers": broker,
            "linger.ms": 5,
            "batch.num.messages": 100,
        }
        self._topic = topic
        self._producer: Optional[Producer] = None

    def connect(self) -> None:
        self._producer = Producer(self._conf)
        logger.info(
            "Kafka producer connected — broker=%s topic=%s",
            self._conf["bootstrap.servers"],
            self._topic,
        )

    def _delivery_callback(self, err, msg) -> None:
        if err:
            logger.error("Message delivery failed: %s", err)
        else:
            logger.debug(
                "Message delivered to %s [%d] @ offset %d",
                msg.topic(),
                msg.partition(),
                msg.offset(),
            )

    def publish(self, event: AnomalyEvent) -> None:
        if self._producer is None:
            raise RuntimeError("Producer is not connected. Call connect() first.")

        payload = event.model_dump_json()
        self._producer.produce(
            topic=self._topic,
            value=payload.encode("utf-8"),
            key=event.metric_id.encode("utf-8"),
            callback=self._delivery_callback,
        )
        self._producer.poll(0)

    def flush(self, timeout: float = 5.0) -> None:
        if self._producer:
            self._producer.flush(timeout)

    def close(self) -> None:
        if self._producer:
            self._producer.flush(10.0)
            logger.info("Kafka producer closed")
