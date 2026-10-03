import logging
import threading
from typing import List, Tuple

import numpy as np
from sklearn.ensemble import IsolationForest

from models import EnrichedMetricEvent, Severity

logger = logging.getLogger(__name__)


class IsolationForestDetector:
    def __init__(
        self,
        n_estimators: int = 100,
        contamination: float = 0.01,
    ):
        self._model = IsolationForest(
            n_estimators=n_estimators,
            contamination=contamination,
            random_state=42,
            n_jobs=-1,
        )
        self._trained = False
        self._lock = threading.Lock()

    @property
    def is_trained(self) -> bool:
        return self._trained

    def _build_feature_matrix(self, events: List[EnrichedMetricEvent]) -> np.ndarray:
        features = np.array([[e.value, e.z_score] for e in events], dtype=np.float64)
        return features

    def train(self, events: List[EnrichedMetricEvent]) -> None:
        if len(events) < 10:
            logger.warning("Not enough events to train: %d", len(events))
            return

        features = self._build_feature_matrix(events)

        with self._lock:
            self._model.fit(features)
            self._trained = True

        logger.info(
            "IsolationForest trained on %d events, feature shape: %s",
            len(events),
            features.shape,
        )

    def predict(self, event: EnrichedMetricEvent) -> Tuple[bool, float, float]:
        if not self._trained:
            raise RuntimeError("Model has not been trained yet")

        features = np.array([[event.value, event.z_score]], dtype=np.float64)

        with self._lock:
            raw_score = self._model.decision_function(features)[0]
            prediction = self._model.predict(features)[0]

        anomaly_score = max(0.0, min(1.0, 0.5 - raw_score))
        confidence = abs(anomaly_score - 0.5) * 2.0
        is_anomaly = prediction == -1

        return is_anomaly, anomaly_score, confidence

    @staticmethod
    def classify_severity(anomaly_score: float, z_score: float) -> Severity:
        abs_z = abs(z_score)

        if anomaly_score > 0.9 or abs_z > 4:
            return Severity.CRITICAL
        if anomaly_score > 0.7 or abs_z > 3:
            return Severity.HIGH
        if anomaly_score > 0.5 or abs_z > 2:
            return Severity.MEDIUM
        return Severity.LOW
