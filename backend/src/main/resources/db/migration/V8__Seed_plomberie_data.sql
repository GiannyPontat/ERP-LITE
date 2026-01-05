-- Migration V8: Seed de données plomberie
-- Repositionnement pour plombiers indépendants

-- =====================================================
-- 1. Nettoyage des anciennes données catalogue BTP
-- =====================================================
DELETE FROM gp_erp_catalog_item;

-- =====================================================
-- 2. Insertion des prestations plomberie
-- =====================================================

-- DÉPLACEMENT
INSERT INTO gp_erp_catalog_item (reference, designation, description, category, unit, unit_price, tax_rate, cost_price, active, created_at, updated_at)
VALUES
('DEPL-01', 'Déplacement zone 1', 'Déplacement dans un rayon de 15 km', 'DEPLACEMENT', 'forfait', 35.00, 20.00, 10.00, true, NOW(), NOW()),
('DEPL-02', 'Déplacement zone 2', 'Déplacement entre 15 et 30 km', 'DEPLACEMENT', 'forfait', 55.00, 20.00, 20.00, true, NOW(), NOW()),
('DEPL-URG', 'Déplacement urgence', 'Déplacement urgence (hors horaires)', 'DEPLACEMENT', 'forfait', 75.00, 20.00, 25.00, true, NOW(), NOW());

-- MAIN D'OEUVRE
INSERT INTO gp_erp_catalog_item (reference, designation, description, category, unit, unit_price, tax_rate, cost_price, active, created_at, updated_at)
VALUES
('MO-STD', 'Main d''œuvre', 'Taux horaire standard', 'MAIN_OEUVRE', 'h', 55.00, 20.00, 25.00, true, NOW(), NOW()),
('MO-URG', 'Main d''œuvre urgence', 'Taux horaire majoré (+50%)', 'MAIN_OEUVRE', 'h', 82.50, 20.00, 25.00, true, NOW(), NOW()),
('MO-WE', 'Main d''œuvre week-end', 'Taux horaire week-end', 'MAIN_OEUVRE', 'h', 75.00, 20.00, 25.00, true, NOW(), NOW());

-- DÉPANNAGE
INSERT INTO gp_erp_catalog_item (reference, designation, description, category, unit, unit_price, tax_rate, cost_price, active, created_at, updated_at)
VALUES
('DEP-FUITE', 'Recherche et réparation fuite', 'Localisation et réparation d''une fuite', 'DEPANNAGE', 'forfait', 120.00, 10.00, 30.00, true, NOW(), NOW()),
('DEP-DEBOU', 'Débouchage canalisation', 'Débouchage manuel ou furet', 'DEPANNAGE', 'forfait', 95.00, 10.00, 20.00, true, NOW(), NOW()),
('DEP-WC', 'Débouchage WC', 'Débouchage toilettes', 'DEPANNAGE', 'forfait', 85.00, 10.00, 15.00, true, NOW(), NOW()),
('DEP-SIPHON', 'Remplacement siphon', 'Dépose et pose siphon', 'DEPANNAGE', 'forfait', 65.00, 10.00, 15.00, true, NOW(), NOW()),
('DEP-JOINT', 'Remplacement joint', 'Changement joint d''étanchéité', 'DEPANNAGE', 'forfait', 45.00, 10.00, 5.00, true, NOW(), NOW()),
('DEP-DIAG', 'Diagnostic panne', 'Recherche de panne et devis', 'DEPANNAGE', 'forfait', 60.00, 20.00, 0.00, true, NOW(), NOW());

-- ROBINETTERIE
INSERT INTO gp_erp_catalog_item (reference, designation, description, category, unit, unit_price, tax_rate, cost_price, active, created_at, updated_at)
VALUES
('ROB-POSE', 'Pose mitigeur', 'Installation mitigeur (fourniture non incluse)', 'ROBINETTERIE', 'u', 45.00, 10.00, 10.00, true, NOW(), NOW()),
('ROB-MELA', 'Mitigeur mélangeur standard', 'Fourniture mitigeur entrée de gamme', 'ROBINETTERIE', 'u', 65.00, 20.00, 35.00, true, NOW(), NOW()),
('ROB-QUAL', 'Mitigeur qualité pro', 'Fourniture mitigeur qualité professionnelle', 'ROBINETTERIE', 'u', 120.00, 20.00, 70.00, true, NOW(), NOW()),
('ROB-THER', 'Mitigeur thermostatique', 'Fourniture mitigeur thermostatique douche', 'ROBINETTERIE', 'u', 180.00, 20.00, 100.00, true, NOW(), NOW()),
('ROB-CUIS', 'Mitigeur évier cuisine', 'Fourniture mitigeur évier avec douchette', 'ROBINETTERIE', 'u', 95.00, 20.00, 50.00, true, NOW(), NOW()),
('ROB-ARR', 'Robinet d''arrêt', 'Fourniture et pose robinet d''arrêt', 'ROBINETTERIE', 'u', 55.00, 10.00, 20.00, true, NOW(), NOW());

-- SANITAIRES
INSERT INTO gp_erp_catalog_item (reference, designation, description, category, unit, unit_price, tax_rate, cost_price, active, created_at, updated_at)
VALUES
('SAN-WC-DEP', 'Dépose WC', 'Démontage WC existant', 'SANITAIRES', 'u', 45.00, 10.00, 10.00, true, NOW(), NOW()),
('SAN-WC-POSE', 'Pose WC', 'Installation WC (fourniture non incluse)', 'SANITAIRES', 'u', 120.00, 10.00, 30.00, true, NOW(), NOW()),
('SAN-WC-STD', 'WC complet standard', 'Fourniture pack WC + abattant', 'SANITAIRES', 'u', 180.00, 20.00, 100.00, true, NOW(), NOW()),
('SAN-WC-SUSP', 'WC suspendu + bâti', 'Fourniture WC suspendu avec bâti-support', 'SANITAIRES', 'u', 450.00, 20.00, 280.00, true, NOW(), NOW()),
('SAN-LAV-DEP', 'Dépose lavabo', 'Démontage lavabo existant', 'SANITAIRES', 'u', 35.00, 10.00, 10.00, true, NOW(), NOW()),
('SAN-LAV-POSE', 'Pose lavabo', 'Installation lavabo (fourniture non incluse)', 'SANITAIRES', 'u', 95.00, 10.00, 25.00, true, NOW(), NOW()),
('SAN-LAV-STD', 'Lavabo standard', 'Fourniture lavabo céramique', 'SANITAIRES', 'u', 85.00, 20.00, 45.00, true, NOW(), NOW()),
('SAN-BIDE-POSE', 'Pose bidet', 'Installation bidet', 'SANITAIRES', 'u', 90.00, 10.00, 25.00, true, NOW(), NOW());

-- CHAUFFE-EAU
INSERT INTO gp_erp_catalog_item (reference, designation, description, category, unit, unit_price, tax_rate, cost_price, active, created_at, updated_at)
VALUES
('CE-DEP', 'Dépose chauffe-eau', 'Démontage chauffe-eau existant', 'CHAUFFE_EAU', 'u', 85.00, 10.00, 20.00, true, NOW(), NOW()),
('CE-POSE', 'Pose chauffe-eau', 'Installation chauffe-eau (fourniture non incluse)', 'CHAUFFE_EAU', 'u', 180.00, 10.00, 40.00, true, NOW(), NOW()),
('CE-RACC', 'Raccordement plomberie', 'Raccordement eau chaude/froide', 'CHAUFFE_EAU', 'u', 95.00, 10.00, 25.00, true, NOW(), NOW()),
('CE-100L', 'Chauffe-eau 100L', 'Fourniture chauffe-eau électrique 100L', 'CHAUFFE_EAU', 'u', 380.00, 20.00, 250.00, true, NOW(), NOW()),
('CE-150L', 'Chauffe-eau 150L', 'Fourniture chauffe-eau électrique 150L', 'CHAUFFE_EAU', 'u', 480.00, 20.00, 320.00, true, NOW(), NOW()),
('CE-200L', 'Chauffe-eau 200L', 'Fourniture chauffe-eau électrique 200L', 'CHAUFFE_EAU', 'u', 580.00, 20.00, 380.00, true, NOW(), NOW()),
('CE-300L', 'Chauffe-eau 300L', 'Fourniture chauffe-eau électrique 300L', 'CHAUFFE_EAU', 'u', 750.00, 20.00, 500.00, true, NOW(), NOW()),
('CE-THERMO', 'Chauffe-eau thermodynamique', 'Fourniture chauffe-eau thermodynamique 200L', 'CHAUFFE_EAU', 'u', 1800.00, 20.00, 1200.00, true, NOW(), NOW()),
('CE-DETART', 'Détartrage chauffe-eau', 'Vidange et détartrage ballon', 'CHAUFFE_EAU', 'u', 120.00, 10.00, 20.00, true, NOW(), NOW()),
('CE-ANODE', 'Remplacement anode', 'Changement anode magnésium', 'CHAUFFE_EAU', 'u', 95.00, 10.00, 35.00, true, NOW(), NOW());

-- CHAUDIÈRE
INSERT INTO gp_erp_catalog_item (reference, designation, description, category, unit, unit_price, tax_rate, cost_price, active, created_at, updated_at)
VALUES
('CHAUD-ENT', 'Entretien chaudière annuel', 'Visite annuelle obligatoire + attestation', 'CHAUDIERE', 'forfait', 120.00, 10.00, 30.00, true, NOW(), NOW()),
('CHAUD-DIAG', 'Diagnostic panne chaudière', 'Recherche de panne', 'CHAUDIERE', 'forfait', 95.00, 20.00, 20.00, true, NOW(), NOW()),
('CHAUD-PURGE', 'Purge radiateurs', 'Purge circuit chauffage', 'CHAUDIERE', 'forfait', 75.00, 10.00, 15.00, true, NOW(), NOW()),
('CHAUD-DESEMB', 'Désembouage circuit', 'Nettoyage circuit chauffage', 'CHAUDIERE', 'forfait', 350.00, 10.00, 100.00, true, NOW(), NOW());

-- CANALISATIONS
INSERT INTO gp_erp_catalog_item (reference, designation, description, category, unit, unit_price, tax_rate, cost_price, active, created_at, updated_at)
VALUES
('CAN-CUI-14', 'Tube cuivre 14mm', 'Fourniture et pose tube cuivre', 'CANALISATIONS', 'ml', 18.00, 20.00, 8.00, true, NOW(), NOW()),
('CAN-CUI-16', 'Tube cuivre 16mm', 'Fourniture et pose tube cuivre', 'CANALISATIONS', 'ml', 22.00, 20.00, 10.00, true, NOW(), NOW()),
('CAN-PER-16', 'Tube PER 16mm', 'Fourniture et pose tube multicouche', 'CANALISATIONS', 'ml', 12.00, 20.00, 5.00, true, NOW(), NOW()),
('CAN-PER-20', 'Tube PER 20mm', 'Fourniture et pose tube multicouche', 'CANALISATIONS', 'ml', 15.00, 20.00, 7.00, true, NOW(), NOW()),
('CAN-PVC-40', 'Tube PVC évacuation 40mm', 'Fourniture et pose tube évacuation', 'CANALISATIONS', 'ml', 12.00, 20.00, 5.00, true, NOW(), NOW()),
('CAN-PVC-100', 'Tube PVC évacuation 100mm', 'Fourniture et pose tube évacuation', 'CANALISATIONS', 'ml', 18.00, 20.00, 8.00, true, NOW(), NOW()),
('CAN-PERC', 'Percement mur/plancher', 'Percement passage canalisation', 'CANALISATIONS', 'u', 35.00, 10.00, 5.00, true, NOW(), NOW()),
('CAN-COLMAT', 'Colmatage percement', 'Rebouchage après percement', 'CANALISATIONS', 'u', 25.00, 10.00, 5.00, true, NOW(), NOW());

-- SALLE DE BAIN
INSERT INTO gp_erp_catalog_item (reference, designation, description, category, unit, unit_price, tax_rate, cost_price, active, created_at, updated_at)
VALUES
('SDB-RECEV', 'Pose receveur douche', 'Installation receveur + étanchéité', 'SALLE_DE_BAIN', 'u', 280.00, 10.00, 60.00, true, NOW(), NOW()),
('SDB-RECEV-F', 'Receveur douche extra-plat', 'Fourniture receveur 90x120', 'SALLE_DE_BAIN', 'u', 280.00, 20.00, 150.00, true, NOW(), NOW()),
('SDB-PAROI', 'Pose paroi douche', 'Installation paroi fixe ou coulissante', 'SALLE_DE_BAIN', 'u', 120.00, 10.00, 30.00, true, NOW(), NOW()),
('SDB-PAROI-F', 'Paroi douche fixe 120cm', 'Fourniture paroi verre 8mm', 'SALLE_DE_BAIN', 'u', 350.00, 20.00, 200.00, true, NOW(), NOW()),
('SDB-COLON', 'Colonne de douche', 'Fourniture colonne thermostatique', 'SALLE_DE_BAIN', 'u', 320.00, 20.00, 180.00, true, NOW(), NOW()),
('SDB-MEUB', 'Pose meuble vasque', 'Installation meuble + vasque', 'SALLE_DE_BAIN', 'u', 150.00, 10.00, 40.00, true, NOW(), NOW()),
('SDB-MEUB-F', 'Meuble vasque 80cm', 'Fourniture meuble + vasque + miroir', 'SALLE_DE_BAIN', 'u', 480.00, 20.00, 280.00, true, NOW(), NOW()),
('SDB-SECHE', 'Sèche-serviettes', 'Fourniture sèche-serviettes électrique', 'SALLE_DE_BAIN', 'u', 250.00, 20.00, 130.00, true, NOW(), NOW()),
('SDB-BAIGN-D', 'Dépose baignoire', 'Démontage baignoire existante', 'SALLE_DE_BAIN', 'u', 120.00, 10.00, 30.00, true, NOW(), NOW()),
('SDB-BAIGN-P', 'Pose baignoire', 'Installation baignoire', 'SALLE_DE_BAIN', 'u', 250.00, 10.00, 60.00, true, NOW(), NOW());

-- CUISINE
INSERT INTO gp_erp_catalog_item (reference, designation, description, category, unit, unit_price, tax_rate, cost_price, active, created_at, updated_at)
VALUES
('CUIS-EVIER-P', 'Pose évier', 'Installation évier cuisine', 'CUISINE', 'u', 85.00, 10.00, 25.00, true, NOW(), NOW()),
('CUIS-LV', 'Raccordement lave-vaisselle', 'Raccordement eau + évacuation', 'CUISINE', 'u', 75.00, 10.00, 15.00, true, NOW(), NOW()),
('CUIS-LL', 'Raccordement lave-linge', 'Raccordement eau + évacuation', 'CUISINE', 'u', 65.00, 10.00, 15.00, true, NOW(), NOW()),
('CUIS-BROYA', 'Installation broyeur', 'Pose broyeur sous évier', 'CUISINE', 'u', 95.00, 10.00, 25.00, true, NOW(), NOW());

-- EXTÉRIEUR
INSERT INTO gp_erp_catalog_item (reference, designation, description, category, unit, unit_price, tax_rate, cost_price, active, created_at, updated_at)
VALUES
('EXT-ROB', 'Robinet extérieur', 'Fourniture et pose robinet jardin', 'EXTERIEUR', 'u', 95.00, 10.00, 40.00, true, NOW(), NOW()),
('EXT-ARR', 'Création arrosage', 'Installation système arrosage', 'EXTERIEUR', 'forfait', 350.00, 10.00, 100.00, true, NOW(), NOW());

-- DIVERS
INSERT INTO gp_erp_catalog_item (reference, designation, description, category, unit, unit_price, tax_rate, cost_price, active, created_at, updated_at)
VALUES
('DIV-FLEX', 'Flexible alimentation', 'Fourniture flexible inox', 'DIVERS', 'u', 18.00, 20.00, 8.00, true, NOW(), NOW()),
('DIV-CLAPET', 'Clapet anti-retour', 'Fourniture et pose clapet', 'DIVERS', 'u', 45.00, 20.00, 20.00, true, NOW(), NOW()),
('DIV-REDUC', 'Réducteur de pression', 'Fourniture et pose réducteur', 'DIVERS', 'u', 120.00, 10.00, 60.00, true, NOW(), NOW()),
('DIV-EVACU', 'Évacuation gravats', 'Enlèvement et mise en décharge', 'DIVERS', 'forfait', 80.00, 20.00, 40.00, true, NOW(), NOW()),
('DIV-NETTOY', 'Nettoyage chantier', 'Nettoyage fin de chantier', 'DIVERS', 'forfait', 50.00, 20.00, 10.00, true, NOW(), NOW());

-- =====================================================
-- 3. Clients fictifs d'exemple
-- =====================================================
-- Note: Ces clients seront associés à l'utilisateur créé lors de l'inscription
-- Ils servent d'exemples pour l'onboarding

-- =====================================================
-- 4. Mise à jour libellés entité Project
-- =====================================================
-- Renommer "Chantier" en "Intervention" dans les références
-- (Les références existantes seront mises à jour à l'utilisation)

