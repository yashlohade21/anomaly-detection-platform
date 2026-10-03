import random

from config import Config
from models import MetricEvent, MetricMetadata


def generate_disk_metric(server_id: str) -> MetricEvent:
    """Generate a disk_io metric with a value normally distributed around 55 MB/s
    (range ~10-100 MB/s under normal conditions)."""
    value = random.gauss(mu=55.0, sigma=22.0)
    value = max(0.0, round(value, 2))

    return MetricEvent(
        source_id=server_id,
        metric_type="disk_io",
        value=value,
        unit="MB/s",
        metadata=MetricMetadata(
            hostname=Config.hostname_for_server(server_id),
            region=Config.region_for_server(server_id),
        ),
    )
