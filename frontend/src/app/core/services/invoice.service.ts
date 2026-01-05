import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Invoice, InvoiceStatus } from '../models/invoice.model';

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {
  private readonly apiUrl = `${environment.apiUrl}/invoices`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(this.apiUrl);
  }

  getById(id: number): Observable<Invoice> {
    return this.http.get<Invoice>(`${this.apiUrl}/${id}`);
  }

  create(invoice: Invoice): Observable<Invoice> {
    return this.http.post<Invoice>(this.apiUrl, invoice);
  }

  update(id: number, invoice: Invoice): Observable<Invoice> {
    return this.http.put<Invoice>(`${this.apiUrl}/${id}`, invoice);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Marque une facture comme payée
   */
  markAsPaid(id: number, paymentDate?: string): Observable<Invoice> {
    return this.http.patch<Invoice>(`${this.apiUrl}/${id}/mark-as-paid`, { paymentDate });
  }

  /**
   * Change le statut d'une facture
   */
  updateStatus(id: number, status: InvoiceStatus): Observable<Invoice> {
    return this.http.patch<Invoice>(`${this.apiUrl}/${id}/status`, { status });
  }

  /**
   * Envoie une facture par email
   */
  sendInvoiceByEmail(invoiceId: number, recipientEmail?: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${invoiceId}/send-email`, { recipientEmail });
  }

  /**
   * Envoie une relance pour facture impayée
   */
  sendReminder(invoiceId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${invoiceId}/send-reminder`, {});
  }

  /**
   * Télécharge le PDF d'une facture
   */
  downloadPdf(invoiceId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${invoiceId}/pdf`, {
      responseType: 'blob'
    });
  }

  /**
   * Génère le PDF d'une facture (alias pour downloadPdf)
   */
  generatePdf(invoiceId: number): Observable<Blob> {
    return this.downloadPdf(invoiceId);
  }

  /**
   * Crée une facture à partir d'un devis
   */
  createFromQuote(quoteId: number): Observable<Invoice> {
    return this.http.post<Invoice>(`${this.apiUrl}/from-quote/${quoteId}`, {});
  }

  /**
   * Duplique une facture
   */
  duplicate(invoiceId: number): Observable<Invoice> {
    return this.http.post<Invoice>(`${this.apiUrl}/${invoiceId}/duplicate`, {});
  }

  /**
   * Récupère les factures par statut
   */
  getByStatus(status: InvoiceStatus): Observable<Invoice[]> {
    const params = new HttpParams().set('status', status);
    return this.http.get<Invoice[]>(this.apiUrl, { params });
  }

  /**
   * Récupère les factures d'un client
   */
  getByClient(clientId: number): Observable<Invoice[]> {
    const params = new HttpParams().set('clientId', clientId.toString());
    return this.http.get<Invoice[]>(this.apiUrl, { params });
  }

  /**
   * Récupère les factures en retard
   */
  getOverdue(): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(`${this.apiUrl}/overdue`);
  }

  /**
   * Recherche de factures
   */
  search(query: string): Observable<Invoice[]> {
    const params = new HttpParams().set('query', query);
    return this.http.get<Invoice[]>(`${this.apiUrl}/search`, { params });
  }
}
