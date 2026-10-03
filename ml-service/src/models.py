from datetime import datetime
from enum import Enum
from typing import Optional

from pydantic import BaseModel, Field


class Severity(str, Enum):
    LOW = "LOW"
    MEDIUM = "MEDIUM"
    HIGH = "HIGH"
    CRITICAL = "CRITICAL"


class EnrichedMetricEvent(BaseModel):
    event_id: str
    source_id: str
    metric_type: str
    value: float
    unit: Optional[str] = None
    timestamp: str
    metadata: Optional[dict] = None
    z_score: float = 0.0
    baseline_mean: Optional[float] = None
    baseline_stddev: Optional[float] = None
    time_bucket: Optional[str] = None
    is_duplicate: Optional[bool] = False


class AnomalyEvent(BaseModel):
    event_id: str
    source_id: str
    metric_type: str
    value: float
    timestamp: str
    anomaly_score: float = Field(ge=0.0, le=1.0)
    z_score: float
    severity: Severity
    confidence: float = Field(ge=0.0, le=1.0)
    model_used: str = "isolation_forest"
    detected_at: str = Field(default_factory=lambda: datetime.utcnow().isoformat() + "Z")


class DetectRequest(BaseModel):
    event_id: str = "manual"
    source_id: str = "manual"
    metric_type: str = "manual"
    value: float
    z_score: float
    timestamp: Optional[str] = None


class DetectResponse(BaseModel):
    is_anomaly: bool
    anomaly_score: float
    confidence: float
    severity: Severity
    model_trained: bool
    message: str
