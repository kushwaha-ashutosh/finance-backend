-- ================================================
-- Finance Backend - PostgreSQL + pgvector Setup
-- Run this script ONCE before starting the app
-- ================================================

-- Step 1: Create database (run as postgres superuser)
-- CREATE DATABASE financedb;

-- Step 2: Connect to financedb
-- \c financedb

-- Step 3: Enable pgvector extension
CREATE EXTENSION IF NOT EXISTS vector;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN','ANALYST','VIEWER')),
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE')),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transactions table with pgvector embedding column
CREATE TABLE IF NOT EXISTS transactions (
    id          BIGSERIAL PRIMARY KEY,
    amount      NUMERIC(12,2) NOT NULL CHECK (amount > 0),
    type        VARCHAR(20) NOT NULL CHECK (type IN ('INCOME','EXPENSE')),
    category    VARCHAR(100) NOT NULL,
    date        DATE NOT NULL,
    notes       TEXT,
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,
    user_id     BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    embedding   vector(8)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_transactions_type       ON transactions(type);
CREATE INDEX IF NOT EXISTS idx_transactions_category   ON transactions(category);
CREATE INDEX IF NOT EXISTS idx_transactions_date       ON transactions(date);
CREATE INDEX IF NOT EXISTS idx_transactions_is_deleted ON transactions(is_deleted);
CREATE INDEX IF NOT EXISTS idx_transactions_user_id    ON transactions(user_id);

-- pgvector IVFFlat cosine similarity index
CREATE INDEX IF NOT EXISTS idx_transactions_embedding
    ON transactions USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 10);
