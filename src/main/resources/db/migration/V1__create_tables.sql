-- Create schema
CREATE SCHEMA IF NOT EXISTS member_mgmt;

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Create member table
CREATE TABLE IF NOT EXISTS member_mgmt.member (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create role table
CREATE TABLE IF NOT EXISTS member_mgmt.role (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Create users table
CREATE TABLE IF NOT EXISTS member_mgmt.users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);

-- Create junction table for user_roles
CREATE TABLE IF NOT EXISTS member_mgmt.user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES member_mgmt.users(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES member_mgmt.role(id) ON DELETE CASCADE
);

-- Insert default roles only if they don't exist
INSERT INTO member_mgmt.role (id, name)
SELECT gen_random_uuid(), 'ROLE_ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM member_mgmt.role WHERE name = 'ROLE_ADMIN'
);

INSERT INTO member_mgmt.role (id, name)
SELECT gen_random_uuid(), 'ROLE_USER'
WHERE NOT EXISTS (
    SELECT 1 FROM member_mgmt.role WHERE name = 'ROLE_USER'
);
