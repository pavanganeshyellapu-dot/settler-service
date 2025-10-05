-- ===================================================================
--  V002__seed_data.sql
--  Purpose: Insert initial reference or sample data for dev testing.
-- ===================================================================

-- Insert a default user (you can update this ID to match your UUID generator)
INSERT INTO users (id, email, display_name, created_at)
VALUES ('00000000-0000-0000-0000-000000000001', 'demo@settler.com', 'Demo User', NOW())
ON CONFLICT DO NOTHING;

-- Optional: Insert a sample group owned by demo user
INSERT INTO groups (id, name, currency_code, owner_id, simplify_balances, allow_member_edits, created_at)
VALUES ('00000000-0000-0000-0000-000000000010', 'Demo Group', 'INR', '00000000-0000-0000-0000-000000000001', FALSE, TRUE, NOW())
ON CONFLICT DO NOTHING;

-- Optional: Link demo user to demo group
INSERT INTO group_members (group_id, user_id, role, joined_at)
VALUES ('00000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000001', 'OWNER', NOW())
ON CONFLICT DO NOTHING;
