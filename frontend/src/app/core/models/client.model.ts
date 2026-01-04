export interface Client {
  id: number;
  companyName?: string;
  siret?: string;
  contactFirstName?: string;
  contactLastName?: string;
  email?: string;
  phone?: string;
  address?: string;
  city?: string;
  postalCode?: string;
  paymentTerms?: number;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
  // Legacy fields for compatibility
  nom?: string;
  entreprise?: string;
  telephone?: string;
  adresse?: string;
}

export interface CreateClientDto {
  companyName: string;
  siret?: string;
  contactFirstName?: string;
  contactLastName?: string;
  email: string;
  phone?: string;
  address?: string;
  city?: string;
  postalCode?: string;
  paymentTerms?: number;
  notes?: string;
}

export interface UpdateClientDto {
  companyName?: string;
  siret?: string;
  contactFirstName?: string;
  contactLastName?: string;
  email?: string;
  phone?: string;
  address?: string;
  city?: string;
  postalCode?: string;
  paymentTerms?: number;
  notes?: string;
}

