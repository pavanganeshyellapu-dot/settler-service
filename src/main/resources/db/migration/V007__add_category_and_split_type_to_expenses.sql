-- =========================================================
-- V007__add_category_and_split_type_to_expenses.sql
-- Purpose: Add category and split_type columns to expenses table
-- =========================================================

ALTER TABLE expenses
ADD COLUMN IF NOT EXISTS category VARCHAR(50),
ADD COLUMN IF NOT EXISTS split_type VARCHAR(20);
