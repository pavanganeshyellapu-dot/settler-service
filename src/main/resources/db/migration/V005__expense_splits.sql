-- =========================================================
-- V005__expense_splits.sql
-- Purpose: Create the 'expense_splits' table for user shares
-- =========================================================

CREATE TABLE IF NOT EXISTS expense_splits (
                                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    expense_id UUID NOT NULL REFERENCES expenses(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount NUMERIC(10,2) NOT NULL CHECK (amount >= 0),
    percentage NUMERIC(5,2) CHECK (percentage >= 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

-- Each user can appear once per expense
CREATE UNIQUE INDEX IF NOT EXISTS ux_expense_user ON expense_splits(expense_id, user_id);
CREATE INDEX IF NOT EXISTS idx_expense_splits_expense_id ON expense_splits(expense_id);
