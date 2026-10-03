import random

from config import Config
from models import MetricEvent, MetricMetadata


def generate_cpu_metric(server_id: str) -> MetricEvent:
    """Generate a cpu_usage metric with a value normally distributed around 50%
    (range ~30-70% under normal conditions)."""
    value = random.gauss(mu=50.0, sigma=10.0)
    value = max(0.0, min(100.0, round(value, 2)))

    return MetricEvent(
        source_id=server_id,
        metric_type="cpu_usage",
        value=value,
        unit="percent",
        metadata=MetricMetadata(
            hostname=Config.hostname_for_server(server_id),
            region=Config.region_for_server(server_id),
        ),
    )
