CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS users_sequence
    START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS transactions (
    id          BIGSERIAL PRIMARY KEY,
    amount      NUMERIC(12,2) NOT NULL,
    type        VARCHAR(20) NOT NULL,
    category    VARCHAR(100) NOT NULL,
    date        DATE NOT NULL,
    notes       TEXT,
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,
    user_id     BIGINT REFERENCES users(id)
                ON DELETE SET NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    embedding   float[]
);

CREATE SEQUENCE IF NOT EXISTS transactions_sequence
    START WITH 1 INCREMENT BY 1;