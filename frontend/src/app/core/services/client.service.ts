import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Client, CreateClientDto, UpdateClientDto } from '../models/client.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private readonly apiUrl = `${environment.apiUrl}/clients`;

  constructor(private http: HttpClient) {}

  getAll(page: number = 0, size: number = 20, search?: string): Observable<Page<Client>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'id,desc');

    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }

    return this.http.get<Page<Client>>(this.apiUrl, { params });
  }

  /**
   * Récupère tous les clients sans pagination (pour les selects)
   */
  getAllSimple(): Observable<Client[]> {
    return this.http.get<Client[]>(`${this.apiUrl}/all`);
  }

  getById(id: number): Observable<Client> {
    return this.http.get<Client>(`${this.apiUrl}/${id}`);
  }

  create(client: CreateClientDto): Observable<Client> {
    return this.http.post<Client>(this.apiUrl, client);
  }

  update(id: number, client: UpdateClientDto): Observable<Client> {
    return this.http.put<Client>(`${this.apiUrl}/${id}`, client);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Recherche avancée de clients
   */
  search(criteria: {
    name?: string;
    email?: string;
    city?: string;
    page?: number;
    size?: number;
  }): Observable<Page<Client>> {
    let params = new HttpParams()
      .set('page', (criteria.page || 0).toString())
      .set('size', (criteria.size || 20).toString())
      .set('sort', 'id,desc');

    if (criteria.name) params = params.set('name', criteria.name);
    if (criteria.email) params = params.set('email', criteria.email);
    if (criteria.city) params = params.set('city', criteria.city);

    return this.http.get<Page<Client>>(this.apiUrl, { params });
  }

  /**
   * Récupère l'historique d'un client (factures, devis, interventions)
   */
  getHistory(clientId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${clientId}/history`);
  }

  /**
   * Récupère les statistiques d'un client
   */
  getStats(clientId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${clientId}/stats`);
  }
}
