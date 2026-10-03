import json
import logging
from typing import Callable, Optional

from confluent_kafka import Consumer, KafkaError, KafkaException

from config import KAFKA_BROKER, TOPIC_ENRICHED_METRICS
from models import EnrichedMetricEvent

logger = logging.getLogger(__name__)


class MetricsConsumer:
    def __init__(
        self,
        broker: str = KAFKA_BROKER,
        topic: str = TOPIC_ENRICHED_METRICS,
        group_id: str = "ml-service-group",
    ):
        self._conf = {
            "bootstrap.servers": broker,
            "group.id": group_id,
            "auto.offset.reset": "latest",
            "enable.auto.commit": True,
            "session.timeout.ms": 30000,
            "heartbeat.interval.ms": 10000,
        }
        self._topic = topic
        self._consumer: Optional[Consumer] = None
        self._running = False

    def start(self, on_event: Callable[[EnrichedMetricEvent], None]) -> None:
        self._consumer = Consumer(self._conf)
        self._consumer.subscribe([self._topic])
        self._running = True

        logger.info(
            "Kafka consumer started — broker=%s topic=%s",
            self._conf["bootstrap.servers"],
            self._topic,
        )

        try:
            while self._running:
                msg = self._consumer.poll(timeout=1.0)
                if msg is None:
                    continue

                err = msg.error()
                if err:
                    if err.code() == KafkaError._PARTITION_EOF:
                        continue
                    logger.error("Consumer error: %s", err)
                    raise KafkaException(err)

                try:
                    payload = json.loads(msg.value().decode("utf-8"))
                    event = EnrichedMetricEvent(**payload)
                    on_event(event)
                except (json.JSONDecodeError, ValueError) as exc:
                    logger.warning("Failed to deserialize message: %s", exc)
        finally:
            self._consumer.close()
            logger.info("Kafka consumer stopped")

    def stop(self) -> None:
        self._running = False
