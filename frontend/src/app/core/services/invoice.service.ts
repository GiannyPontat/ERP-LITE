import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Invoice } from '../models/invoice.model';

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

  createFromQuote(quoteId: number, invoice: Invoice): Observable<Invoice> {
    return this.http.post<Invoice>(`${this.apiUrl}/from-quote/${quoteId}`, invoice);
  }

  update(id: number, invoice: Invoice): Observable<Invoice> {
    return this.http.put<Invoice>(`${this.apiUrl}/${id}`, invoice);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getByClientId(clientId: number): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(`${this.apiUrl}/client/${clientId}`);
  }

  getByStatus(status: string): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(`${this.apiUrl}/status/${status}`);
  }

  /**
   * Génère un PDF pour la facture
   */
  generatePdf(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/pdf`, {
      responseType: 'blob'
    });
  }

  /**
   * Envoie la facture par email
   */
  sendInvoiceByEmail(invoiceId: number, recipientEmail: string): Observable<string> {
    const params = new HttpParams().set('email', recipientEmail);
    return this.http.post(`${this.apiUrl}/${invoiceId}/send-email`, null, {
      params,
      responseType: 'text'
    });
  }

  /**
   * Envoie une relance pour facture impayée
   */
  sendInvoiceReminder(invoiceId: number, recipientEmail: string): Observable<string> {
    const params = new HttpParams().set('email', recipientEmail);
    return this.http.post(`${this.apiUrl}/${invoiceId}/send-reminder`, null, {
      params,
      responseType: 'text'
    });
  }

  /**
   * Marque une facture comme payée
   */
  markAsPaid(invoiceId: number, paidDate?: string): Observable<Invoice> {
    const body = paidDate ? { paidDate } : null;
    return this.http.patch<Invoice>(`${this.apiUrl}/${invoiceId}/mark-as-paid`, body);
  }
}

