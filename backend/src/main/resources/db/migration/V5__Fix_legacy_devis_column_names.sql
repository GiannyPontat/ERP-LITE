-- Migration V5: Fix column names in legacy Devis tables to match Java entity field naming
-- Hibernate expects lowercase field names when no @Column annotation is present

-- Drop and recreate Devis table with correct column names
DROP TABLE IF EXISTS gp_erp_lignedevis CASCADE;
DROP TABLE IF EXISTS gp_erp_devis CASCADE;

-- Recreate Devis table with Hibernate-compatible column names
CREATE TABLE gp_erp_devis (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT,
    datecreation DATE,
    statut VARCHAR(50),
    totalht DOUBLE PRECISION,
    totalttc DOUBLE PRECISION,
    FOREIGN KEY (client_id) REFERENCES gp_erp_client(id) ON DELETE SET NULL
);

-- Recreate LigneDevis table with Hibernate-compatible column names
CREATE TABLE gp_erp_lignedevis (
    id BIGSERIAL PRIMARY KEY,
    devis_id BIGINT,
    description VARCHAR(255) NOT NULL,
    quantite INTEGER NOT NULL CHECK (quantite >= 1),
    prixunitaire DOUBLE PRECISION,
    FOREIGN KEY (devis_id) REFERENCES gp_erp_devis(id) ON DELETE CASCADE
);

-- Recreate indexes
CREATE INDEX idx_devis_client ON gp_erp_devis(client_id);
CREATE INDEX idx_lignedevis_devis ON gp_erp_lignedevis(devis_id);

-- Add comments
COMMENT ON TABLE gp_erp_devis IS 'Legacy Devis table - kept for backward compatibility. New quotes should use gp_erp_quote table.';
COMMENT ON TABLE gp_erp_lignedevis IS 'Legacy LigneDevis table - kept for backward compatibility. New quote items should use gp_erp_quote_item table.';
