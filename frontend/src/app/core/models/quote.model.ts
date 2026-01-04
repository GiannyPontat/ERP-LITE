import { Client } from './client.model';

export enum QuoteStatus {
  DRAFT = 'DRAFT',
  SENT = 'SENT',
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED',
  EXPIRED = 'EXPIRED',
  CONVERTED = 'CONVERTED'
}

export interface QuoteItem {
  id?: number;
  description: string;
  quantity: number;
  unitPrice: number;
  total?: number;
}

export interface Quote {
  id?: number;
  quoteNumber?: string;
  clientId?: number;
  client?: Client;
  createdBy?: number;
  date: string;
  validUntil?: string;
  status: QuoteStatus;
  subtotal?: number;
  taxRate?: number;
  taxAmount?: number;
  total?: number;
  notes?: string;
  termsAndConditions?: string;
  items: QuoteItem[];
  clientName?: string;
  createdByEmail?: string;
}

