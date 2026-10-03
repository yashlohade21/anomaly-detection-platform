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
    metric_id: str
    service_name: str
    metric_name: str
    value: float
    timestamp: str
    z_score: float
    rolling_mean: Optional[float] = None
    rolling_std: Optional[float] = None
    tags: Optional[dict] = None


class AnomalyEvent(BaseModel):
    metric_id: str
    service_name: str
    metric_name: str
    value: float
    timestamp: str
    anomaly_score: float = Field(ge=0.0, le=1.0)
    z_score: float
    severity: Severity
    confidence: float = Field(ge=0.0, le=1.0)
    model_used: str = "IsolationForest"
    detected_at: str = Field(default_factory=lambda: datetime.utcnow().isoformat() + "Z")


class DetectRequest(BaseModel):
    metric_id: str = "manual"
    service_name: str = "manual"
    metric_name: str = "manual"
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
