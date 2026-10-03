CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(50) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE data_sources (
    id BIGSERIAL PRIMARY KEY,
    source_id VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    hostname VARCHAR(255),
    region VARCHAR(100),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE anomaly_events (
    id BIGSERIAL PRIMARY KEY,
    event_id UUID UNIQUE NOT NULL,
    source_id VARCHAR(100) NOT NULL,
    metric_type VARCHAR(100) NOT NULL,
    value DOUBLE PRECISION NOT NULL,
    anomaly_score DOUBLE PRECISION,
    z_score DOUBLE PRECISION,
    severity VARCHAR(20) NOT NULL,
    confidence DOUBLE PRECISION,
    model_used VARCHAR(50),
    timestamp TIMESTAMP NOT NULL,
    detected_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE alert_rules (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    source_id VARCHAR(100),
    metric_type VARCHAR(100),
    severity_threshold VARCHAR(20) DEFAULT 'HIGH',
    enabled BOOLEAN DEFAULT TRUE,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_anomaly_events_severity ON anomaly_events(severity);
CREATE INDEX idx_anomaly_events_source ON anomaly_events(source_id);
CREATE INDEX idx_anomaly_events_timestamp ON anomaly_events(timestamp DESC);
