-- Initial Schema Migration for ERP-LITE
-- This migration creates all database tables and initial data

-- Create Role table
CREATE TABLE IF NOT EXISTS gp_erp_role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Create User table
CREATE TABLE IF NOT EXISTS gp_erp_user (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE
);

-- Create User-Role join table
CREATE TABLE IF NOT EXISTS gp_erp_user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES gp_erp_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES gp_erp_role(id) ON DELETE CASCADE
);

-- Create Client table
CREATE TABLE IF NOT EXISTS gp_erp_client (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255),
    entreprise VARCHAR(255),
    email VARCHAR(255),
    telephone VARCHAR(50),
    adresse TEXT
);

-- Create Devis table
CREATE TABLE IF NOT EXISTS gp_erp_devis (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT,
    date_creation DATE,
    statut VARCHAR(50),
    total_ht DOUBLE PRECISION,
    total_ttc DOUBLE PRECISION,
    FOREIGN KEY (client_id) REFERENCES gp_erp_client(id) ON DELETE SET NULL
);

-- Create LigneDevis table
CREATE TABLE IF NOT EXISTS gp_erp_lignedevis (
    id BIGSERIAL PRIMARY KEY,
    devis_id BIGINT,
    description VARCHAR(255) NOT NULL,
    quantite INTEGER NOT NULL CHECK (quantite >= 1),
    prix_unitaire DOUBLE PRECISION,
    FOREIGN KEY (devis_id) REFERENCES gp_erp_devis(id) ON DELETE CASCADE
);

-- Create VerificationToken table
CREATE TABLE IF NOT EXISTS gp_erp_verification_token (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    type VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES gp_erp_user(id) ON DELETE CASCADE
);

-- Create RefreshToken table
CREATE TABLE IF NOT EXISTS gp_erp_refresh_token (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES gp_erp_user(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_user_email ON gp_erp_user(email);
CREATE INDEX IF NOT EXISTS idx_verification_token_token ON gp_erp_verification_token(token);
CREATE INDEX IF NOT EXISTS idx_verification_token_user ON gp_erp_verification_token(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_token_token ON gp_erp_refresh_token(token);
CREATE INDEX IF NOT EXISTS idx_refresh_token_user ON gp_erp_refresh_token(user_id);
CREATE INDEX IF NOT EXISTS idx_devis_client ON gp_erp_devis(client_id);
CREATE INDEX IF NOT EXISTS idx_lignedevis_devis ON gp_erp_lignedevis(devis_id);

-- Insert initial roles
INSERT INTO gp_erp_role (name) VALUES ('ADMIN'), ('USER')
ON CONFLICT (name) DO NOTHING;

