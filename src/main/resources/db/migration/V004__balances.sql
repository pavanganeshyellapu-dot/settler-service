-- ===================================================================
--  V004__balances.sql
--  Purpose: Add group balance tracking for fast retrieval of debts.
--  Author: Settler Backend
-- ===================================================================

-- 🔹 Create group_balances table
CREATE TABLE IF NOT EXISTS group_balances (
    id UUID PRIMARY KEY,
    group_id UUID NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    balance NUMERIC(14, 2) NOT NULL DEFAULT 0.00,
    currency_code VARCHAR(8) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    CONSTRAINT uq_group_balance UNIQUE (group_id, user_id)
);

-- 🔹 Indexes for faster lookups
CREATE INDEX IF NOT EXISTS idx_group_balances_group_id ON group_balances (group_id);
CREATE INDEX IF NOT EXISTS idx_group_balances_user_id ON group_balances (user_id);

-- 🔹 Optional: a trigger to keep updated_at consistent
CREATE OR REPLACE FUNCTION update_balance_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_update_balance_timestamp ON group_balances;

CREATE TRIGGER trg_update_balance_timestamp
BEFORE UPDATE ON group_balances
FOR EACH ROW
EXECUTE FUNCTION update_balance_timestamp();

-- 🔹 Insert default entries (optional example)
-- Each user in a group should have a row with 0 balance at creation
-- This can also be done at the service layer instead.
