from datetime import datetime, timezone
from uuid import uuid4

from pydantic import BaseModel, Field


class MetricMetadata(BaseModel):
    """Additional context attached to every metric event."""

    hostname: str
    region: str


class MetricEvent(BaseModel):
    """A single metric measurement emitted by the data simulator."""

    event_id: str = Field(default_factory=lambda: str(uuid4()))
    source_id: str
    metric_type: str
    value: float
    unit: str
    timestamp: str = Field(
        default_factory=lambda: datetime.now(timezone.utc).isoformat()
    )
    metadata: MetricMetadata

    def to_json_bytes(self) -> bytes:
        """Serialize the event to UTF-8 encoded JSON bytes for Kafka."""
        return self.model_dump_json().encode("utf-8")
