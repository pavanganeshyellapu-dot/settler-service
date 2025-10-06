CREATE TABLE IF NOT EXISTS expense_splits (
                                              id UUID PRIMARY KEY,
                                              expense_id UUID NOT NULL REFERENCES expenses(id),
    user_id UUID NOT NULL REFERENCES users(id),
    share_amount NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
    );
