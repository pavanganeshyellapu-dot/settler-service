-- Add gender and date_of_birth if missing
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS gender VARCHAR(10),
    ADD COLUMN IF NOT EXISTS date_of_birth DATE;

-- Make email and phone optional
ALTER TABLE users
    ALTER COLUMN email DROP NOT NULL,
    ALTER COLUMN phone DROP NOT NULL;
