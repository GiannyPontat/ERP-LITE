/**
 * Catégories d'articles du catalogue BTP/Plomberie
 */
export enum CatalogCategory {
  ROBINETTERIE = 'ROBINETTERIE',
  TUYAUTERIE = 'TUYAUTERIE',
  CHAUFFAGE = 'CHAUFFAGE',
  SANITAIRE = 'SANITAIRE',
  OUTILLAGE = 'OUTILLAGE',
  ACCESSOIRES = 'ACCESSOIRES',
  MAIN_OEUVRE = 'MAIN_OEUVRE'
}

/**
 * Labels français des catégories
 */
export const CATALOG_CATEGORY_LABELS: Record<CatalogCategory, string> = {
  [CatalogCategory.ROBINETTERIE]: 'Robinetterie',
  [CatalogCategory.TUYAUTERIE]: 'Tuyauterie',
  [CatalogCategory.CHAUFFAGE]: 'Chauffage',
  [CatalogCategory.SANITAIRE]: 'Sanitaire',
  [CatalogCategory.OUTILLAGE]: 'Outillage',
  [CatalogCategory.ACCESSOIRES]: 'Accessoires',
  [CatalogCategory.MAIN_OEUVRE]: 'Main d\'œuvre'
};

/**
 * Unités de mesure
 */
export enum CatalogUnit {
  PIECE = 'PIECE',
  METRE = 'METRE',
  KG = 'KG',
  HEURE = 'HEURE',
  FORFAIT = 'FORFAIT'
}

/**
 * Labels français des unités
 */
export const CATALOG_UNIT_LABELS: Record<CatalogUnit, string> = {
  [CatalogUnit.PIECE]: 'pièce',
  [CatalogUnit.METRE]: 'mètre',
  [CatalogUnit.KG]: 'kg',
  [CatalogUnit.HEURE]: 'heure',
  [CatalogUnit.FORFAIT]: 'forfait'
};

/**
 * Modèle d'article du catalogue
 */
export interface CatalogItem {
  id?: number;
  reference: string;
  name: string;
  description?: string;
  category: CatalogCategory;
  unitPrice: number;
  unit: CatalogUnit;
  taxRate?: number;
  supplier?: string;
  stock?: number;
  minStock?: number;
  isActive?: boolean;
  imageUrl?: string;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}

/**
 * DTO pour créer un article
 */
export interface CreateCatalogItemDto {
  reference: string;
  name: string;
  description?: string;
  category: CatalogCategory;
  unitPrice: number;
  unit: CatalogUnit;
  taxRate?: number;
  supplier?: string;
  stock?: number;
  minStock?: number;
  notes?: string;
}

/**
 * DTO pour mettre à jour un article
 */
export interface UpdateCatalogItemDto {
  reference?: string;
  name?: string;
  description?: string;
  category?: CatalogCategory;
  unitPrice?: number;
  unit?: CatalogUnit;
  taxRate?: number;
  supplier?: string;
  stock?: number;
  minStock?: number;
  isActive?: boolean;
  notes?: string;
}
