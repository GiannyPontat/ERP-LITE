-- Migration V3: Migrate legacy Devis data to Quote system and drop legacy tables
-- This migration moves existing Devis/LigneDevis data to the new Quote/QuoteItem structure

-- Step 1: Create a temporary mapping table to track migrated records
CREATE TEMP TABLE devis_quote_mapping (
    old_devis_id BIGINT,
    new_quote_id BIGINT
);

-- Step 2: Migrate Devis records to Quote table
INSERT INTO gp_erp_quote (
    quote_number,
    client_id,
    created_by,
    date,
    valid_until,
    status,
    subtotal,
    tax_rate,
    tax_amount,
    total,
    notes,
    terms_and_conditions
)
SELECT
    -- Generate quote number based on old devis ID and creation date
    CONCAT('DEV-', EXTRACT(YEAR FROM COALESCE(d.date_creation, CURRENT_DATE)), '-', LPAD(d.id::TEXT, 4, '0')),
    -- Use client_id if valid, otherwise use the first available client
    COALESCE(d.client_id, (SELECT MIN(id) FROM gp_erp_client)),
    -- Use the first admin user as created_by
    (SELECT id FROM gp_erp_user
     WHERE id IN (SELECT user_id FROM gp_erp_user_roles WHERE role_id IN (SELECT id FROM gp_erp_role WHERE name = 'ADMIN'))
     LIMIT 1),
    -- Use creation date or current date
    COALESCE(d.date_creation, CURRENT_DATE),
    -- Set valid_until to 30 days from creation date
    COALESCE(d.date_creation, CURRENT_DATE) + INTERVAL '30 days',
    -- Map status: convert French statut to English QuoteStatus enum
    CASE
        WHEN UPPER(d.statut) IN ('BROUILLON', 'DRAFT') THEN 'DRAFT'
        WHEN UPPER(d.statut) IN ('ENVOYE', 'SENT', 'ENVOYÉ') THEN 'SENT'
        WHEN UPPER(d.statut) IN ('ACCEPTE', 'ACCEPTED', 'ACCEPTÉ') THEN 'ACCEPTED'
        WHEN UPPER(d.statut) IN ('REFUSE', 'REJECTED', 'REFUSÉ') THEN 'REJECTED'
        WHEN UPPER(d.statut) IN ('EXPIRE', 'EXPIRED', 'EXPIRÉ') THEN 'EXPIRED'
        WHEN UPPER(d.statut) IN ('CONVERTI', 'CONVERTED') THEN 'CONVERTED'
        ELSE 'DRAFT'
    END,
    -- Convert double precision to NUMERIC(19,2)
    COALESCE(d.total_ht, 0)::NUMERIC(19,2),
    -- Calculate tax rate from totalHT and totalTTC (default 20% if calculation fails)
    CASE
        WHEN d.total_ht > 0 THEN ((d.total_ttc - d.total_ht) / d.total_ht * 100)::NUMERIC(5,2)
        ELSE 20.00
    END,
    -- Calculate tax amount
    COALESCE(d.total_ttc - d.total_ht, 0)::NUMERIC(19,2),
    -- Total TTC
    COALESCE(d.total_ttc, 0)::NUMERIC(19,2),
    -- Notes
    CONCAT('Migrated from legacy Devis #', d.id),
    -- Terms and conditions (empty for legacy data)
    NULL
FROM gp_erp_devis d
WHERE d.id IS NOT NULL;

-- Step 3: Populate the mapping table
INSERT INTO devis_quote_mapping (old_devis_id, new_quote_id)
SELECT
    d.id,
    q.id
FROM gp_erp_devis d
JOIN gp_erp_quote q ON q.quote_number = CONCAT('DEV-', EXTRACT(YEAR FROM COALESCE(d.date_creation, CURRENT_DATE)), '-', LPAD(d.id::TEXT, 4, '0'))
WHERE d.id IS NOT NULL;

-- Step 4: Migrate LigneDevis records to QuoteItem table
INSERT INTO gp_erp_quote_item (
    quote_id,
    description,
    quantity,
    unit_price,
    total
)
SELECT
    m.new_quote_id,
    ld.description,
    ld.quantite,
    ld.prix_unitaire::NUMERIC(19,2),
    (ld.quantite * ld.prix_unitaire)::NUMERIC(19,2)
FROM gp_erp_lignedevis ld
JOIN devis_quote_mapping m ON ld.devis_id = m.old_devis_id
WHERE ld.id IS NOT NULL AND ld.quantite >= 1;

-- Step 5: Verify migration counts
DO $$
DECLARE
    devis_count INTEGER;
    quote_count INTEGER;
    ligne_devis_count INTEGER;
    quote_item_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO devis_count FROM gp_erp_devis;
    SELECT COUNT(*) INTO quote_count FROM devis_quote_mapping;
    SELECT COUNT(*) INTO ligne_devis_count FROM gp_erp_lignedevis;
    SELECT COUNT(*) INTO quote_item_count FROM gp_erp_quote_item WHERE quote_id IN (SELECT new_quote_id FROM devis_quote_mapping);

    RAISE NOTICE 'Migration Summary:';
    RAISE NOTICE 'Devis migrated: % out of %', quote_count, devis_count;
    RAISE NOTICE 'LigneDevis migrated: % out of %', quote_item_count, ligne_devis_count;

    IF quote_count < devis_count THEN
        RAISE WARNING 'Some Devis records may not have been migrated!';
    END IF;
END $$;

-- Step 6: Drop the legacy tables
DROP TABLE IF EXISTS gp_erp_lignedevis CASCADE;
DROP TABLE IF EXISTS gp_erp_devis CASCADE;

-- Step 7: Drop the temporary mapping table
DROP TABLE IF EXISTS devis_quote_mapping;

-- Add comments to document the migration
COMMENT ON TABLE gp_erp_quote IS 'Quote table - includes migrated legacy Devis records (migrated via V3)';
COMMENT ON TABLE gp_erp_quote_item IS 'Quote item table - includes migrated legacy LigneDevis records (migrated via V3)';
