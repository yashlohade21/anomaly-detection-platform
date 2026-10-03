import os

from dotenv import load_dotenv

load_dotenv()


class Config:
    """Application configuration loaded from environment variables with sensible defaults."""

    KAFKA_BROKER: str = os.getenv("KAFKA_BROKER", "localhost:9092")
    KAFKA_TOPIC: str = os.getenv("KAFKA_TOPIC", "raw-metrics")

    NUM_SERVERS: int = int(os.getenv("NUM_SERVERS", "10"))
    PUBLISH_INTERVAL_SEC: float = float(os.getenv("PUBLISH_INTERVAL_SEC", "1.0"))

    ANOMALY_PROBABILITY: float = float(os.getenv("ANOMALY_PROBABILITY", "0.05"))

    REGIONS: list[str] = [
        "us-east-1",
        "us-west-2",
        "eu-west-1",
        "ap-southeast-1",
    ]

    @classmethod
    def server_ids(cls) -> list[str]:
        """Return a list of server identifiers from server-001 to server-{NUM_SERVERS}."""
        return [f"server-{i:03d}" for i in range(1, cls.NUM_SERVERS + 1)]

    @classmethod
    def region_for_server(cls, server_id: str) -> str:
        """Deterministically assign a region to a server based on its numeric suffix."""
        idx = int(server_id.split("-")[1]) % len(cls.REGIONS)
        return cls.REGIONS[idx]

    @classmethod
    def hostname_for_server(cls, server_id: str) -> str:
        """Generate a hostname for the given server id."""
        region = cls.region_for_server(server_id)
        return f"{server_id}.{region}.internal"
