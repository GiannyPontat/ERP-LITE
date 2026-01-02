-- Migration V2: Add Quote and Invoice tables
-- This migration adds new tables for Quote/Invoice system and updates existing tables

-- Add new columns to User table
ALTER TABLE gp_erp_user
ADD COLUMN IF NOT EXISTS uuid UUID UNIQUE DEFAULT gen_random_uuid(),
ADD COLUMN IF NOT EXISTS user_role VARCHAR(50),
ADD COLUMN IF NOT EXISTS company VARCHAR(255),
ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- Add new columns to Client table
ALTER TABLE gp_erp_client
ADD COLUMN IF NOT EXISTS company_name VARCHAR(255),
ADD COLUMN IF NOT EXISTS siret VARCHAR(255),
ADD COLUMN IF NOT EXISTS contact_first_name VARCHAR(255),
ADD COLUMN IF NOT EXISTS contact_last_name VARCHAR(255),
ADD COLUMN IF NOT EXISTS phone VARCHAR(50),
ADD COLUMN IF NOT EXISTS address TEXT,
ADD COLUMN IF NOT EXISTS city VARCHAR(255),
ADD COLUMN IF NOT EXISTS postal_code VARCHAR(20),
ADD COLUMN IF NOT EXISTS payment_terms INTEGER,
ADD COLUMN IF NOT EXISTS notes TEXT,
ADD COLUMN IF NOT EXISTS user_id BIGINT;

-- Add foreign key constraint for Client.user_id
ALTER TABLE gp_erp_client
ADD CONSTRAINT fk_client_user 
FOREIGN KEY (user_id) REFERENCES gp_erp_user(id) ON DELETE SET NULL;

-- Create Quote table
CREATE TABLE IF NOT EXISTS gp_erp_quote (
    id BIGSERIAL PRIMARY KEY,
    quote_number VARCHAR(50) NOT NULL UNIQUE,
    client_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    date DATE NOT NULL,
    valid_until DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    subtotal NUMERIC(19, 2) NOT NULL DEFAULT 0,
    tax_rate NUMERIC(5, 2) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(19, 2) NOT NULL DEFAULT 0,
    total NUMERIC(19, 2) NOT NULL DEFAULT 0,
    notes TEXT,
    terms_and_conditions TEXT,
    FOREIGN KEY (client_id) REFERENCES gp_erp_client(id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES gp_erp_user(id) ON DELETE RESTRICT
);

-- Create QuoteItem table
CREATE TABLE IF NOT EXISTS gp_erp_quote_item (
    id BIGSERIAL PRIMARY KEY,
    quote_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity >= 1),
    unit_price NUMERIC(19, 2) NOT NULL,
    total NUMERIC(19, 2) NOT NULL,
    FOREIGN KEY (quote_id) REFERENCES gp_erp_quote(id) ON DELETE CASCADE
);

-- Create Invoice table
CREATE TABLE IF NOT EXISTS gp_erp_invoice (
    id BIGSERIAL PRIMARY KEY,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    client_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    quote_id BIGINT,
    date DATE NOT NULL,
    due_date DATE,
    paid_date DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    subtotal NUMERIC(19, 2) NOT NULL DEFAULT 0,
    tax_rate NUMERIC(5, 2) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(19, 2) NOT NULL DEFAULT 0,
    total NUMERIC(19, 2) NOT NULL DEFAULT 0,
    notes TEXT,
    terms_and_conditions TEXT,
    FOREIGN KEY (client_id) REFERENCES gp_erp_client(id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES gp_erp_user(id) ON DELETE RESTRICT,
    FOREIGN KEY (quote_id) REFERENCES gp_erp_quote(id) ON DELETE SET NULL
);

-- Create InvoiceItem table
CREATE TABLE IF NOT EXISTS gp_erp_invoice_item (
    id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity >= 1),
    unit_price NUMERIC(19, 2) NOT NULL,
    total NUMERIC(19, 2) NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES gp_erp_invoice(id) ON DELETE CASCADE
);

-- Create sequences for auto-generating quote and invoice numbers
CREATE SEQUENCE IF NOT EXISTS quote_number_seq;
CREATE SEQUENCE IF NOT EXISTS invoice_number_seq;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_user_uuid ON gp_erp_user(uuid);
CREATE INDEX IF NOT EXISTS idx_client_user ON gp_erp_client(user_id);
CREATE INDEX IF NOT EXISTS idx_quote_number ON gp_erp_quote(quote_number);
CREATE INDEX IF NOT EXISTS idx_quote_client ON gp_erp_quote(client_id);
CREATE INDEX IF NOT EXISTS idx_quote_created_by ON gp_erp_quote(created_by);
CREATE INDEX IF NOT EXISTS idx_quote_status ON gp_erp_quote(status);
CREATE INDEX IF NOT EXISTS idx_quote_item_quote ON gp_erp_quote_item(quote_id);
CREATE INDEX IF NOT EXISTS idx_invoice_number ON gp_erp_invoice(invoice_number);
CREATE INDEX IF NOT EXISTS idx_invoice_client ON gp_erp_invoice(client_id);
CREATE INDEX IF NOT EXISTS idx_invoice_created_by ON gp_erp_invoice(created_by);
CREATE INDEX IF NOT EXISTS idx_invoice_quote ON gp_erp_invoice(quote_id);
CREATE INDEX IF NOT EXISTS idx_invoice_status ON gp_erp_invoice(status);
CREATE INDEX IF NOT EXISTS idx_invoice_item_invoice ON gp_erp_invoice_item(invoice_id);

