"""Data Simulator -- generates realistic server metrics and publishes them to Kafka."""

import logging
import signal
import sys
import time

from config import Config
from kafka_producer import MetricProducer
from generators.cpu_generator import generate_cpu_metric
from generators.memory_generator import generate_memory_metric
from generators.disk_generator import generate_disk_metric
from generators.network_generator import generate_network_metric
from generators.anomaly_injector import maybe_inject_anomaly

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s - %(message)s",
    datefmt="%Y-%m-%dT%H:%M:%S",
)
logger = logging.getLogger("data-simulator")

# All generator functions keyed by metric type for easy iteration.
GENERATORS = [
    generate_cpu_metric,
    generate_memory_metric,
    generate_disk_metric,
    generate_network_metric,
]

_running = True


def _shutdown(signum, _frame):
    global _running
    logger.info("Received signal %s, shutting down gracefully", signum)
    _running = False


def main() -> None:
    signal.signal(signal.SIGINT, _shutdown)
    signal.signal(signal.SIGTERM, _shutdown)

    server_ids = Config.server_ids()
    topic = Config.KAFKA_TOPIC

    logger.info(
        "Starting data simulator: servers=%d, topic=%s, broker=%s, interval=%.1fs",
        len(server_ids),
        topic,
        Config.KAFKA_BROKER,
        Config.PUBLISH_INTERVAL_SEC,
    )

    producer = MetricProducer(broker=Config.KAFKA_BROKER)

    try:
        while _running:
            for server_id in server_ids:
                for generator_fn in GENERATORS:
                    event = generator_fn(server_id)
                    event = maybe_inject_anomaly(event)
                    producer.publish(topic, event)
                    logger.info(
                        "Published: source_id=%s metric_type=%s value=%.2f",
                        event.source_id,
                        event.metric_type,
                        event.value,
                    )

            time.sleep(Config.PUBLISH_INTERVAL_SEC)
    finally:
        producer.close()
        logger.info("Data simulator stopped")


if __name__ == "__main__":
    main()
