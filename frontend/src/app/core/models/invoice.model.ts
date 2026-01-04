import { Client } from './client.model';
import { Quote } from './quote.model';

export enum InvoiceStatus {
  DRAFT = 'DRAFT',
  SENT = 'SENT',
  PAID = 'PAID',
  OVERDUE = 'OVERDUE',
  CANCELLED = 'CANCELLED',
  PARTIALLY_PAID = 'PARTIALLY_PAID'
}

export interface InvoiceItem {
  id?: number;
  description: string;
  quantity: number;
  unitPrice: number;
  total?: number;
}

export interface Invoice {
  id?: number;
  invoiceNumber?: string;
  clientId?: number;
  client?: Client;
  quoteId?: number;
  quote?: Quote;
  createdBy?: number;
  date: string;
  dueDate?: string;
  paidDate?: string;
  status: InvoiceStatus;
  subtotal?: number;
  taxRate?: number;
  taxAmount?: number;
  total?: number;
  notes?: string;
  termsAndConditions?: string;
  items: InvoiceItem[];
  clientName?: string;
  createdByEmail?: string;
  quoteNumber?: string;
}

