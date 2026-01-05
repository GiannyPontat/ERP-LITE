-- =====================================================
-- V7: Add Catalog, Projects, and Client Portal tables
-- =====================================================

-- =====================================================
-- 1. CATALOG TABLE (Bibliothèque de prix BTP)
-- =====================================================
CREATE TABLE IF NOT EXISTS gp_erp_catalog_item (
    id BIGSERIAL PRIMARY KEY,
    reference VARCHAR(50) NOT NULL UNIQUE,
    designation VARCHAR(500) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    tax_rate DECIMAL(5, 2) DEFAULT 20.00,
    cost_price DECIMAL(10, 2),
    supplier VARCHAR(255),
    brand VARCHAR(100),
    manufacturer_reference VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    notes TEXT,
    user_id BIGINT REFERENCES gp_erp_user(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for catalog
CREATE INDEX IF NOT EXISTS idx_catalog_reference ON gp_erp_catalog_item(reference);
CREATE INDEX IF NOT EXISTS idx_catalog_category ON gp_erp_catalog_item(category);
CREATE INDEX IF NOT EXISTS idx_catalog_designation ON gp_erp_catalog_item(designation);
CREATE INDEX IF NOT EXISTS idx_catalog_active ON gp_erp_catalog_item(active);

-- =====================================================
-- 2. PROJECT TABLE (Gestion des chantiers)
-- =====================================================
CREATE TABLE IF NOT EXISTS gp_erp_project (
    id BIGSERIAL PRIMARY KEY,
    reference VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    client_id BIGINT NOT NULL REFERENCES gp_erp_client(id),
    manager_id BIGINT REFERENCES gp_erp_user(id),
    created_by_id BIGINT NOT NULL REFERENCES gp_erp_user(id),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    site_address VARCHAR(500),
    site_city VARCHAR(100),
    site_postal_code VARCHAR(20),
    start_date DATE,
    end_date DATE,
    actual_start_date DATE,
    actual_end_date DATE,
    estimated_budget DECIMAL(12, 2),
    actual_cost DECIMAL(12, 2) DEFAULT 0,
    progress_percentage INTEGER DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for project
CREATE INDEX IF NOT EXISTS idx_project_reference ON gp_erp_project(reference);
CREATE INDEX IF NOT EXISTS idx_project_client ON gp_erp_project(client_id);
CREATE INDEX IF NOT EXISTS idx_project_status ON gp_erp_project(status);
CREATE INDEX IF NOT EXISTS idx_project_manager ON gp_erp_project(manager_id);

-- =====================================================
-- 3. PROJECT DOCUMENT TABLE (Documents chantier)
-- =====================================================
CREATE TABLE IF NOT EXISTS gp_erp_project_document (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES gp_erp_project(id) ON DELETE CASCADE,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(100),
    file_size BIGINT,
    document_type VARCHAR(50) DEFAULT 'OTHER',
    description TEXT,
    uploaded_by_id BIGINT REFERENCES gp_erp_user(id),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for project documents
CREATE INDEX IF NOT EXISTS idx_project_doc_project ON gp_erp_project_document(project_id);
CREATE INDEX IF NOT EXISTS idx_project_doc_type ON gp_erp_project_document(document_type);

-- =====================================================
-- 4. CLIENT PORTAL ACCESS TABLE (Portail client)
-- =====================================================
CREATE TABLE IF NOT EXISTS gp_erp_client_portal_access (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL REFERENCES gp_erp_client(id) ON DELETE CASCADE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    email_verified BOOLEAN DEFAULT FALSE,
    verification_token VARCHAR(255),
    reset_token VARCHAR(255),
    reset_token_expiry TIMESTAMP,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for client portal
CREATE INDEX IF NOT EXISTS idx_portal_email ON gp_erp_client_portal_access(email);
CREATE INDEX IF NOT EXISTS idx_portal_client ON gp_erp_client_portal_access(client_id);

-- =====================================================
-- 5. ADD PROJECT_ID TO QUOTE AND INVOICE TABLES
-- =====================================================
ALTER TABLE gp_erp_quote ADD COLUMN IF NOT EXISTS project_id BIGINT REFERENCES gp_erp_project(id);
ALTER TABLE gp_erp_invoice ADD COLUMN IF NOT EXISTS project_id BIGINT REFERENCES gp_erp_project(id);

CREATE INDEX IF NOT EXISTS idx_quote_project ON gp_erp_quote(project_id);
CREATE INDEX IF NOT EXISTS idx_invoice_project ON gp_erp_invoice(project_id);

-- =====================================================
-- 6. INSERT SAMPLE CATALOG DATA (BTP Products)
-- =====================================================
INSERT INTO gp_erp_catalog_item (reference, designation, description, category, unit, unit_price, tax_rate, active) VALUES
-- Électricité
('ELEC-001', 'Prise électrique 2P+T', 'Prise électrique encastrable 2 pôles + terre, 16A', 'ELECTRICITE', 'u', 12.50, 20.00, true),
('ELEC-002', 'Interrupteur simple', 'Interrupteur simple allumage encastrable', 'ELECTRICITE', 'u', 8.50, 20.00, true),
('ELEC-003', 'Tableau électrique 2 rangées', 'Coffret électrique 2 rangées 26 modules', 'ELECTRICITE', 'u', 85.00, 20.00, true),
('ELEC-004', 'Câble R2V 3G2.5', 'Câble électrique rigide 3x2.5mm² par mètre', 'ELECTRICITE', 'ml', 2.80, 20.00, true),
('ELEC-005', 'Disjoncteur 16A', 'Disjoncteur divisionnaire 16A courbe C', 'ELECTRICITE', 'u', 15.00, 20.00, true),

-- Plomberie
('PLOMB-001', 'Tube cuivre 14/16', 'Tube cuivre diamètre 14/16 mm par mètre', 'PLOMBERIE', 'ml', 8.50, 20.00, true),
('PLOMB-002', 'Robinet d''arrêt 1/2"', 'Vanne d''arrêt laiton 1/2 pouce', 'PLOMBERIE', 'u', 12.00, 20.00, true),
('PLOMB-003', 'WC complet', 'Pack WC sortie horizontale avec abattant', 'PLOMBERIE', 'u', 189.00, 20.00, true),
('PLOMB-004', 'Lavabo céramique', 'Lavabo céramique blanc avec colonne', 'PLOMBERIE', 'u', 95.00, 20.00, true),
('PLOMB-005', 'Chauffe-eau 200L', 'Chauffe-eau électrique vertical 200L', 'PLOMBERIE', 'u', 450.00, 20.00, true),

-- Peinture
('PEINT-001', 'Peinture acrylique mat', 'Peinture acrylique mate blanche 10L', 'PEINTURE', 'u', 45.00, 20.00, true),
('PEINT-002', 'Enduit de rebouchage', 'Enduit de rebouchage en pâte 5kg', 'PEINTURE', 'u', 18.00, 20.00, true),
('PEINT-003', 'Sous-couche universelle', 'Primaire d''accrochage universel 10L', 'PEINTURE', 'u', 55.00, 20.00, true),
('PEINT-004', 'Main d''oeuvre peinture', 'Pose peinture 2 couches au m²', 'PEINTURE', 'm²', 22.00, 20.00, true),

-- Carrelage
('CARR-001', 'Carrelage grès cérame 60x60', 'Carrelage sol grès cérame 60x60cm', 'CARRELAGE', 'm²', 35.00, 20.00, true),
('CARR-002', 'Faïence murale 20x60', 'Carrelage mural faïence 20x60cm', 'CARRELAGE', 'm²', 28.00, 20.00, true),
('CARR-003', 'Colle carrelage C2', 'Mortier colle amélioré C2 25kg', 'CARRELAGE', 'u', 18.00, 20.00, true),
('CARR-004', 'Joint carrelage', 'Mortier joint fin 5kg', 'CARRELAGE', 'u', 12.00, 20.00, true),
('CARR-005', 'Pose carrelage sol', 'Main d''oeuvre pose carrelage sol au m²', 'CARRELAGE', 'm²', 38.00, 20.00, true),

-- Maçonnerie
('MAC-001', 'Parpaing 20x20x50', 'Bloc béton creux 20x20x50cm', 'MACONNERIE', 'u', 1.80, 20.00, true),
('MAC-002', 'Béton prêt à l''emploi', 'Béton prêt à l''emploi C25/30 au m³', 'MACONNERIE', 'm³', 120.00, 20.00, true),
('MAC-003', 'Mortier bâtard', 'Mortier bâtard prêt à l''emploi 25kg', 'MACONNERIE', 'u', 8.50, 20.00, true),
('MAC-004', 'Ferraillage HA10', 'Acier à béton HA10 par mètre linéaire', 'MACONNERIE', 'ml', 2.50, 20.00, true),

-- Menuiserie
('MENU-001', 'Porte intérieure', 'Porte intérieure isoplane 83x204cm', 'MENUISERIE', 'u', 95.00, 20.00, true),
('MENU-002', 'Fenêtre PVC 2 vantaux', 'Fenêtre PVC blanc 2 vantaux 120x135cm', 'MENUISERIE', 'u', 285.00, 20.00, true),
('MENU-003', 'Porte d''entrée alu', 'Porte d''entrée aluminium vitrée', 'MENUISERIE', 'u', 850.00, 20.00, true),
('MENU-004', 'Pose menuiserie', 'Main d''oeuvre pose menuiserie à l''unité', 'MENUISERIE', 'u', 120.00, 20.00, true),

-- Isolation
('ISOL-001', 'Laine de verre 100mm', 'Laine de verre R=2.5 épaisseur 100mm', 'ISOLATION', 'm²', 8.50, 20.00, true),
('ISOL-002', 'Polystyrène expansé 80mm', 'Isolant PSE blanc 80mm', 'ISOLATION', 'm²', 6.00, 20.00, true),
('ISOL-003', 'Plaque de plâtre BA13', 'Plaque de plâtre standard 13mm', 'PLATRERIE', 'm²', 4.50, 20.00, true),
('ISOL-004', 'Rail placo R48', 'Rail métallique pour cloison R48', 'PLATRERIE', 'ml', 1.80, 20.00, true),

-- Main d'oeuvre
('MO-001', 'Heure d''électricien', 'Main d''oeuvre électricien qualifié', 'ELECTRICITE', 'h', 45.00, 20.00, true),
('MO-002', 'Heure de plombier', 'Main d''oeuvre plombier qualifié', 'PLOMBERIE', 'h', 48.00, 20.00, true),
('MO-003', 'Heure de maçon', 'Main d''oeuvre maçon qualifié', 'MACONNERIE', 'h', 42.00, 20.00, true),
('MO-004', 'Heure de carreleur', 'Main d''oeuvre carreleur qualifié', 'CARRELAGE', 'h', 40.00, 20.00, true),
('MO-005', 'Heure de peintre', 'Main d''oeuvre peintre qualifié', 'PEINTURE', 'h', 38.00, 20.00, true)
ON CONFLICT (reference) DO NOTHING;

