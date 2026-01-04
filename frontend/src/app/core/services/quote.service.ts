import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Quote } from '../models/quote.model';

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

  getByClientId(clientId: number): Observable<Quote[]> {
    return this.http.get<Quote[]>(`${this.apiUrl}/client/${clientId}`);
  }

  getByStatus(status: string): Observable<Quote[]> {
    return this.http.get<Quote[]>(`${this.apiUrl}/status/${status}`);
  }
}

