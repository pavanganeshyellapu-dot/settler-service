-- V002__init_groups_expenses.sql
-- Create Groups Table
CREATE TABLE IF NOT EXISTS groups (
                                      id UUID PRIMARY KEY,
                                      name VARCHAR(255) NOT NULL,
    currency_code VARCHAR(10) NOT NULL,
    owner_id UUID NOT NULL REFERENCES users(id),
    simplify_balances BOOLEAN DEFAULT FALSE,
    allow_member_edits BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
    );

-- Create Expenses Table
CREATE TABLE IF NOT EXISTS expenses (
                                        id UUID PRIMARY KEY,
                                        group_id UUID NOT NULL REFERENCES groups(id),
    paid_by UUID NOT NULL REFERENCES users(id),
    description TEXT,
    amount NUMERIC(10,2) NOT NULL,
    split_type VARCHAR(20) DEFAULT 'EQUAL',  -- EQUAL, PERCENTAGE, SHARE
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
    );

-- Create Expense Splits Table
CREATE TABLE IF NO  T EXISTS expense_splits (
                                              id UUID PRIMARY KEY,
                                              expense_id UUID NOT NULL REFERENCES expenses(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id),
    amount NUMERIC(10,2) NOT NULL,
    percentage NUMERIC(5,2),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
    );

