/**
 * Template de devis prédéfini
 */
export interface QuoteTemplate {
  id: string;
  name: string;
  description: string;
  icon: string;
  category: string;
  defaultValidityDays: number;
  defaultTaxRate: number;
  items: QuoteTemplateItem[];
  conditions: string;
}

export interface QuoteTemplateItem {
  reference: string;
  designation: string;
  description?: string;
  quantity: number;
  unit: string;
  unitPrice: number;
  taxRate: number;
}

/**
 * Templates de devis plomberie prédéfinis
 */
export const QUOTE_TEMPLATES: QuoteTemplate[] = [
  // ================================
  // TEMPLATE 1: Dépannage Urgence
  // ================================
  {
    id: 'depannage-urgence',
    name: 'Dépannage urgence',
    description: 'Intervention rapide (fuite, WC bouché, panne...)',
    icon: 'warning',
    category: 'Dépannage',
    defaultValidityDays: 1,
    defaultTaxRate: 10,
    items: [
      {
        reference: 'DEPL-URG',
        designation: 'Déplacement urgence',
        description: 'Intervention hors horaires ouvrés',
        quantity: 1,
        unit: 'forfait',
        unitPrice: 75.00,
        taxRate: 20
      },
      {
        reference: 'MO-URG',
        designation: 'Main d\'œuvre urgence',
        description: 'Taux horaire majoré',
        quantity: 1,
        unit: 'h',
        unitPrice: 82.50,
        taxRate: 10
      },
      {
        reference: 'DEP-DIAG',
        designation: 'Diagnostic panne',
        description: 'Recherche de l\'origine du problème',
        quantity: 1,
        unit: 'forfait',
        unitPrice: 0,
        taxRate: 10
      }
    ],
    conditions: `⚡ INTERVENTION URGENTE

• Intervention immédiate ou sous 2h maximum
• Règlement sur place (CB, espèces, chèque)
• Garantie pièces : 2 ans
• Devis valable 24h

Ce devis peut évoluer selon les constatations sur place.`
  },

  // ================================
  // TEMPLATE 2: Rénovation Salle de Bain
  // ================================
  {
    id: 'renovation-sdb',
    name: 'Rénovation salle de bain',
    description: 'Transformation complète ou partielle',
    icon: 'bathtub',
    category: 'Rénovation',
    defaultValidityDays: 30,
    defaultTaxRate: 10,
    items: [
      // Dépose
      {
        reference: 'SDB-BAIGN-D',
        designation: 'Dépose baignoire existante',
        description: 'Démontage et évacuation',
        quantity: 1,
        unit: 'u',
        unitPrice: 120.00,
        taxRate: 10
      },
      {
        reference: 'SAN-LAV-DEP',
        designation: 'Dépose lavabo existant',
        quantity: 1,
        unit: 'u',
        unitPrice: 35.00,
        taxRate: 10
      },
      {
        reference: 'SAN-WC-DEP',
        designation: 'Dépose WC existant',
        quantity: 1,
        unit: 'u',
        unitPrice: 45.00,
        taxRate: 10
      },
      {
        reference: 'DIV-EVACU',
        designation: 'Évacuation gravats',
        quantity: 1,
        unit: 'forfait',
        unitPrice: 80.00,
        taxRate: 20
      },
      // Plomberie
      {
        reference: 'CAN-PER-16',
        designation: 'Création alimentation douche',
        description: 'Tube multicouche',
        quantity: 8,
        unit: 'ml',
        unitPrice: 12.00,
        taxRate: 10
      },
      {
        reference: 'CAN-PVC-40',
        designation: 'Modification évacuations',
        quantity: 6,
        unit: 'ml',
        unitPrice: 12.00,
        taxRate: 10
      },
      // Fournitures
      {
        reference: 'SDB-RECEV-F',
        designation: 'Receveur douche extra-plat 90x120',
        quantity: 1,
        unit: 'u',
        unitPrice: 280.00,
        taxRate: 20
      },
      {
        reference: 'SDB-PAROI-F',
        designation: 'Paroi douche fixe 120cm',
        description: 'Verre 8mm traitement anticalcaire',
        quantity: 1,
        unit: 'u',
        unitPrice: 350.00,
        taxRate: 20
      },
      {
        reference: 'SDB-COLON',
        designation: 'Colonne de douche thermostatique',
        quantity: 1,
        unit: 'u',
        unitPrice: 320.00,
        taxRate: 20
      },
      {
        reference: 'SAN-WC-SUSP',
        designation: 'WC suspendu + bâti-support',
        quantity: 1,
        unit: 'u',
        unitPrice: 450.00,
        taxRate: 20
      },
      {
        reference: 'SDB-MEUB-F',
        designation: 'Meuble vasque 80cm + miroir',
        quantity: 1,
        unit: 'u',
        unitPrice: 480.00,
        taxRate: 20
      },
      {
        reference: 'ROB-QUAL',
        designation: 'Mitigeur vasque qualité pro',
        quantity: 1,
        unit: 'u',
        unitPrice: 120.00,
        taxRate: 20
      },
      {
        reference: 'SDB-SECHE',
        designation: 'Sèche-serviettes électrique',
        quantity: 1,
        unit: 'u',
        unitPrice: 250.00,
        taxRate: 20
      },
      // Pose
      {
        reference: 'SDB-RECEV',
        designation: 'Pose receveur + étanchéité',
        quantity: 1,
        unit: 'u',
        unitPrice: 280.00,
        taxRate: 10
      },
      {
        reference: 'SDB-PAROI',
        designation: 'Pose paroi de douche',
        quantity: 1,
        unit: 'u',
        unitPrice: 120.00,
        taxRate: 10
      },
      {
        reference: 'SAN-WC-POSE',
        designation: 'Pose WC suspendu',
        quantity: 1,
        unit: 'u',
        unitPrice: 180.00,
        taxRate: 10
      },
      {
        reference: 'SDB-MEUB',
        designation: 'Pose meuble vasque',
        quantity: 1,
        unit: 'u',
        unitPrice: 150.00,
        taxRate: 10
      }
    ],
    conditions: `📅 RÉNOVATION SALLE DE BAIN

Durée estimée : 3 à 4 jours ouvrés

MODALITÉS DE PAIEMENT :
• Acompte 30% à la signature du devis
• 40% au démarrage des travaux
• Solde 30% à la réception des travaux

GARANTIES :
• Garantie main d'œuvre : 10 ans (décennale)
• Garantie pièces : 2 ans constructeur
• Étanchéité : garantie 10 ans

Ce devis comprend la fourniture et la pose.
Hors carrelage, peinture et électricité.`
  },

  // ================================
  // TEMPLATE 3: Contrat Entretien Annuel
  // ================================
  {
    id: 'entretien-annuel',
    name: 'Contrat entretien chaudière',
    description: 'Visite annuelle obligatoire + attestation',
    icon: 'event_repeat',
    category: 'Entretien',
    defaultValidityDays: 15,
    defaultTaxRate: 10,
    items: [
      {
        reference: 'CHAUD-ENT',
        designation: 'Contrat entretien annuel chaudière',
        description: `Comprend :
- 1 visite d'entretien annuel obligatoire
- Nettoyage brûleur et veilleuse
- Contrôle des organes de sécurité
- Analyse des fumées
- Attestation d'entretien (obligatoire assurance)
- Conseils d'utilisation`,
        quantity: 1,
        unit: 'forfait',
        unitPrice: 140.00,
        taxRate: 10
      },
      {
        reference: 'DEPL-01',
        designation: 'Déplacement inclus zone 1',
        quantity: 1,
        unit: 'forfait',
        unitPrice: 0,
        taxRate: 10
      }
    ],
    conditions: `📋 CONTRAT D'ENTRETIEN ANNUEL

FORMULE SÉRÉNITÉ - Inclus :
✓ 1 visite d'entretien annuel obligatoire
✓ Attestation d'entretien (obligatoire assurance)
✓ Déplacement inclus (zone 1 < 15km)
✓ -15% sur pièces détachées hors contrat
✓ Intervention prioritaire sous 48h en cas de panne

DURÉE : 12 mois, renouvelable tacitement
Résiliation possible avec préavis de 1 mois.

RÈGLEMENT :
• À la signature ou en 2x sans frais
• Par prélèvement automatique possible

📞 Ligne directe dépannage dédiée aux clients sous contrat`
  }
];

/**
 * Récupère un template par son ID
 */
export function getQuoteTemplateById(id: string): QuoteTemplate | undefined {
  return QUOTE_TEMPLATES.find(t => t.id === id);
}

