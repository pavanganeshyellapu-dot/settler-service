-- ==========================================================
--  V001__init.sql
--  Initial schema for Settler backend
--  Author: Settler Team
--  Description:
--    Defines tables for users, groups, group_members,
--    expenses, and expense_splits.
-- ==========================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "citext";

-- ========================
-- USERS
-- ========================
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email CITEXT UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- ========================
-- GROUPS
-- ========================
CREATE TABLE groups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    currency_code VARCHAR(5) DEFAULT 'INR' NOT NULL,
    owner_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    simplify_balances BOOLEAN DEFAULT FALSE,
    allow_member_edits BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- ========================
-- GROUP MEMBERS
-- ========================
CREATE TABLE group_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id UUID NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) DEFAULT 'MEMBER' NOT NULL,
    joined_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UNIQUE (group_id, user_id)
);

-- ========================
-- EXPENSES
-- ========================
CREATE TABLE expenses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id UUID NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    description VARCHAR(255) NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    paid_by UUID NOT NULL REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- ========================
-- EXPENSE SPLITS
-- ========================
CREATE TABLE expense_splits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    expense_id UUID NOT NULL REFERENCES expenses(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    share NUMERIC(12,2) NOT NULL
);

-- ========================
-- INDEXES
-- ========================
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_groups_owner_id ON groups(owner_id);
CREATE INDEX idx_group_members_group_id ON group_members(group_id);
CREATE INDEX idx_expenses_group_id ON expenses(group_id);
CREATE INDEX idx_expense_splits_expense_id ON expense_splits(expense_id);

-- ========================
-- AUDIT / DEBUG COMMENT
-- ========================
COMMENT ON TABLE users IS 'Application users';
COMMENT ON TABLE groups IS 'Expense-sharing groups';
COMMENT ON TABLE group_members IS 'Users belonging to groups';
COMMENT ON TABLE expenses IS 'Expenses added within groups';
COMMENT ON TABLE expense_splits IS 'Breakdown of each expense per member';
