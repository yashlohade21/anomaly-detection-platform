import random

from config import Config
from models import MetricEvent, MetricMetadata


def generate_network_metric(server_id: str) -> MetricEvent:
    """Generate a network_throughput metric with a value normally distributed
    around 275 Mbps (range ~50-500 Mbps under normal conditions)."""
    value = random.gauss(mu=275.0, sigma=110.0)
    value = max(0.0, round(value, 2))

    return MetricEvent(
        source_id=server_id,
        metric_type="network_throughput",
        value=value,
        unit="Mbps",
        metadata=MetricMetadata(
            hostname=Config.hostname_for_server(server_id),
            region=Config.region_for_server(server_id),
        ),
    )
