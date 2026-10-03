import logging

from confluent_kafka import Producer, KafkaError, KafkaException

from models import MetricEvent

logger = logging.getLogger(__name__)


class MetricProducer:
    """Thin wrapper around confluent_kafka.Producer that handles JSON serialization
    and delivery reporting for MetricEvent instances."""

    def __init__(self, broker: str) -> None:
        self._producer = Producer(
            {
                "bootstrap.servers": broker,
                "client.id": "data-simulator",
                "acks": "all",
                "retries": 5,
                "retry.backoff.ms": 500,
                "linger.ms": 5,
                "batch.num.messages": 100,
            }
        )
        logger.info("Kafka producer initialised (broker=%s)", broker)

    @staticmethod
    def _delivery_callback(err: KafkaError | None, msg) -> None:
        """Called once per message to report delivery success or failure."""
        if err is not None:
            logger.error(
                "Delivery failed for event on %s [%s]: %s",
                msg.topic(),
                msg.partition(),
                err,
            )
        else:
            logger.debug(
                "Delivered to %s [%s] @ offset %s",
                msg.topic(),
                msg.partition(),
                msg.offset(),
            )

    def publish(self, topic: str, event: MetricEvent) -> None:
        """Serialize a MetricEvent to JSON and produce it to the given Kafka topic.

        Uses the source_id as the message key so that all events for the same
        server land on the same partition.
        """
        try:
            self._producer.produce(
                topic=topic,
                key=event.source_id.encode("utf-8"),
                value=event.to_json_bytes(),
                callback=self._delivery_callback,
            )
            # Trigger delivery callbacks without blocking
            self._producer.poll(0)
        except KafkaException as exc:
            logger.error(
                "Failed to enqueue event %s: %s", event.event_id, exc
            )
        except BufferError:
            logger.warning(
                "Local producer queue full; flushing before retry"
            )
            self._producer.flush(timeout=5)
            self._producer.produce(
                topic=topic,
                key=event.source_id.encode("utf-8"),
                value=event.to_json_bytes(),
                callback=self._delivery_callback,
            )

    def flush(self, timeout: float = 10.0) -> None:
        """Block until all outstanding messages are delivered or timeout expires."""
        remaining = self._producer.flush(timeout=timeout)
        if remaining > 0:
            logger.warning(
                "%d message(s) still in queue after flush timeout", remaining
            )

    def close(self) -> None:
        """Flush remaining messages and release resources."""
        logger.info("Shutting down Kafka producer")
        self.flush()
