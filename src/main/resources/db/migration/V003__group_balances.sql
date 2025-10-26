-- V003__group_balances.sql
-- Create Group Balances Table
CREATE TABLE IF NOT EXISTS group_balances (
    id UUID PRIMARY KEY,
    group_id UUID NOT NULL REFERENCES groups(id),
    user_id UUID NOT NULL REFERENCES users(id),
    balance NUMERIC(10,2) DEFAULT 0,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
    );
