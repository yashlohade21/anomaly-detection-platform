import random

from config import Config
from models import MetricEvent, MetricMetadata


def generate_memory_metric(server_id: str) -> MetricEvent:
    """Generate a memory_usage metric with a value normally distributed around 57.5%
    (range ~40-75% under normal conditions)."""
    value = random.gauss(mu=57.5, sigma=9.0)
    value = max(0.0, min(100.0, round(value, 2)))

    return MetricEvent(
        source_id=server_id,
        metric_type="memory_usage",
        value=value,
        unit="percent",
        metadata=MetricMetadata(
            hostname=Config.hostname_for_server(server_id),
            region=Config.region_for_server(server_id),
        ),
    )
