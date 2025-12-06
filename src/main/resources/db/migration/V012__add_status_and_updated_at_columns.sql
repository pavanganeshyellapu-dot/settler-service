ALTER TABLE users
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS status VARCHAR(20);

-- Optional fixes for type consistency (safe to run)
ALTER TABLE users
    ALTER COLUMN last_login_at TYPE TIMESTAMPTZ USING last_login_at::timestamptz;

ALTER TABLE users
    ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at::timestamptz;
