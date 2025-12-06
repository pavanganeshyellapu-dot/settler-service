CREATE TABLE IF NOT EXISTS otp_sessions (
    id UUID PRIMARY KEY,

    identifier VARCHAR(255) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    otp_hash VARCHAR(255) NOT NULL,

    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    verified BOOLEAN NOT NULL,
    attempts INT NOT NULL,

    purpose VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
