-- SQL script for creating the users table in PostgreSQL
-- Run this script to create the database schema

-- Create database (run as superuser)
-- CREATE DATABASE user_service_db;

-- Connect to the database
-- \c user_service_db;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    age INTEGER CHECK (age >= 0 AND age <= 150),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_name ON users(name);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

-- Insert sample data (optional)
INSERT INTO users (name, email, age) VALUES 
    ('John Doe', 'john.doe@example.com', 30),
    ('Jane Smith', 'jane.smith@example.com', 25),
    ('Bob Johnson', 'bob.johnson@example.com', 35)
ON CONFLICT (email) DO NOTHING;

-- Grant permissions (if using separate user)
-- GRANT ALL PRIVILEGES ON TABLE users TO user_service_user;
-- GRANT USAGE, SELECT ON SEQUENCE users_id_seq TO user_service_user;

