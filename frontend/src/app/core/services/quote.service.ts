import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Quote, QuoteStatus } from '../models/quote.model';
import { Invoice } from '../models/invoice.model';

@Injectable({
  providedIn: 'root'
})
export class QuoteService {
  private readonly apiUrl = `${environment.apiUrl}/quotes`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Quote[]> {
    return this.http.get<Quote[]>(this.apiUrl);
  }

  getById(id: number): Observable<Quote> {
    return this.http.get<Quote>(`${this.apiUrl}/${id}`);
  }

  create(quote: Quote): Observable<Quote> {
    return this.http.post<Quote>(this.apiUrl, quote);
  }

  update(id: number, quote: Quote): Observable<Quote> {
    return this.http.put<Quote>(`${this.apiUrl}/${id}`, quote);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Change le statut d'un devis
   */
  updateStatus(id: number, status: QuoteStatus): Observable<Quote> {
    return this.http.patch<Quote>(`${this.apiUrl}/${id}/status`, { status });
  }

  /**
   * Convertit un devis en facture
   */
  convertToInvoice(quoteId: number): Observable<Invoice> {
    return this.http.post<Invoice>(`${this.apiUrl}/${quoteId}/convert-to-invoice`, {});
  }

  /**
   * Envoie un devis par email
   */
  sendQuoteByEmail(quoteId: number, recipientEmail?: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${quoteId}/send-email`, { recipientEmail });
  }

  /**
   * Télécharge le PDF d'un devis
   */
  downloadPdf(quoteId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${quoteId}/pdf`, {
      responseType: 'blob'
    });
  }

  /**
   * Génère le PDF d'un devis (alias pour download PDF)
   */
  generatePdf(quoteId: number): Observable<Blob> {
    return this.downloadPdf(quoteId);
  }

  /**
   * Duplique un devis
   */
  duplicate(quoteId: number): Observable<Quote> {
    return this.http.post<Quote>(`${this.apiUrl}/${quoteId}/duplicate`, {});
  }

  /**
   * Récupère les devis par statut
   */
  getByStatus(status: QuoteStatus): Observable<Quote[]> {
    const params = new HttpParams().set('status', status);
    return this.http.get<Quote[]>(this.apiUrl, { params });
  }

  /**
   * Récupère les devis d'un client
   */
  getByClient(clientId: number): Observable<Quote[]> {
    const params = new HttpParams().set('clientId', clientId.toString());
    return this.http.get<Quote[]>(this.apiUrl, { params });
  }

  /**
   * Recherche de devis
   */
  search(query: string): Observable<Quote[]> {
    const params = new HttpParams().set('query', query);
    return this.http.get<Quote[]>(`${this.apiUrl}/search`, { params });
  }
}
