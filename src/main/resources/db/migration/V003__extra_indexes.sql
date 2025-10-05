-- ===================================================================
--  V003__extra_indexes.sql
--  Purpose: Add performance indexes for frequent lookups and joins.
-- ===================================================================

-- Index for quick lookups of user by email
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);

-- Index for finding groups by owner quickly
CREATE INDEX IF NOT EXISTS idx_groups_owner_id ON groups (owner_id);

-- Index for finding group members quickly
CREATE INDEX IF NOT EXISTS idx_group_members_group_id ON group_members (group_id);
CREATE INDEX IF NOT EXISTS idx_group_members_user_id ON group_members (user_id);

-- Index for retrieving expenses by group
CREATE INDEX IF NOT EXISTS idx_expenses_group_id ON expenses (group_id);

-- Index for retrieving splits by expense
CREATE INDEX IF NOT EXISTS idx_expense_splits_expense_id ON expense_splits (expense_id);
