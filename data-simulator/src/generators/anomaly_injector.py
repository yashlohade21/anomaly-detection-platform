import random
import logging

from models import MetricEvent
from config import Config

logger = logging.getLogger(__name__)

# Flatline values per metric type -- a suspiciously constant reading.
_FLATLINE_VALUES: dict[str, float] = {
    "cpu_usage": 50.0,
    "memory_usage": 60.0,
    "disk_io": 0.0,
    "network_throughput": 0.0,
}

# Upper-bound spike ranges per metric type.
_SPIKE_RANGES: dict[str, tuple[float, float]] = {
    "cpu_usage": (95.0, 100.0),
    "memory_usage": (95.0, 100.0),
    "disk_io": (450.0, 600.0),
    "network_throughput": (900.0, 1200.0),
}

# Near-zero drop ranges per metric type.
_DROP_RANGES: dict[str, tuple[float, float]] = {
    "cpu_usage": (0.0, 2.0),
    "memory_usage": (0.0, 3.0),
    "disk_io": (0.0, 1.0),
    "network_throughput": (0.0, 2.0),
}


def maybe_inject_anomaly(event: MetricEvent) -> MetricEvent:
    """With a probability of ~5% (configurable), mutate the event value to
    simulate an anomaly.  Three anomaly patterns are possible:

    1. **Spike** -- value jumps to an extreme high.
    2. **Drop** -- value falls to near zero.
    3. **Flatline** -- value is set to an exact constant (same every time).

    The event is modified in place and returned for convenience.
    """
    if random.random() > Config.ANOMALY_PROBABILITY:
        return event

    anomaly_type = random.choice(["spike", "drop", "flatline"])
    metric = event.metric_type

    if anomaly_type == "spike":
        lo, hi = _SPIKE_RANGES[metric]
        event.value = round(random.uniform(lo, hi), 2)
    elif anomaly_type == "drop":
        lo, hi = _DROP_RANGES[metric]
        event.value = round(random.uniform(lo, hi), 2)
    else:  # flatline
        event.value = _FLATLINE_VALUES[metric]

    logger.warning(
        "ANOMALY injected: type=%s server=%s metric=%s value=%.2f",
        anomaly_type,
        event.source_id,
        event.metric_type,
        event.value,
    )
    return event
