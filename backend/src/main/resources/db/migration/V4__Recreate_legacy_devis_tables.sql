-- Migration V4: Recreate legacy Devis tables for backward compatibility
-- These tables were dropped in V3 but the entities still exist in code
-- This allows the application to start while we gradually migrate away from legacy code

-- Recreate Devis table with column names matching Java entity field names
CREATE TABLE IF NOT EXISTS gp_erp_devis (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT,
    datecreation DATE,
    statut VARCHAR(50),
    totalht DOUBLE PRECISION,
    totalttc DOUBLE PRECISION,
    FOREIGN KEY (client_id) REFERENCES gp_erp_client(id) ON DELETE SET NULL
);

-- Recreate LigneDevis table with column names matching Java entity field names
CREATE TABLE IF NOT EXISTS gp_erp_lignedevis (
    id BIGSERIAL PRIMARY KEY,
    devis_id BIGINT,
    description VARCHAR(255) NOT NULL,
    quantite INTEGER NOT NULL CHECK (quantite >= 1),
    prixunitaire DOUBLE PRECISION,
    FOREIGN KEY (devis_id) REFERENCES gp_erp_devis(id) ON DELETE CASCADE
);

-- Recreate indexes
CREATE INDEX IF NOT EXISTS idx_devis_client ON gp_erp_devis(client_id);
CREATE INDEX IF NOT EXISTS idx_lignedevis_devis ON gp_erp_lignedevis(devis_id);

-- Add comment
COMMENT ON TABLE gp_erp_devis IS 'Legacy Devis table - kept for backward compatibility. New quotes should use gp_erp_quote table.';
COMMENT ON TABLE gp_erp_lignedevis IS 'Legacy LigneDevis table - kept for backward compatibility. New quote items should use gp_erp_quote_item table.';
