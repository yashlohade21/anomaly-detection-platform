import os
from dotenv import load_dotenv

load_dotenv()

KAFKA_BROKER = os.getenv("KAFKA_BROKER", "localhost:9092")

TOPIC_ENRICHED_METRICS = "enriched-metrics"
TOPIC_ANOMALY_EVENTS = "anomaly-events"

ML_BUFFER_SIZE = int(os.getenv("ML_BUFFER_SIZE", "1000"))
ML_RETRAIN_INTERVAL = int(os.getenv("ML_RETRAIN_INTERVAL", "10000"))
