import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Intervention,
  InterventionStatus,
  InterventionType
} from '../models/intervention.model';

@Injectable({
  providedIn: 'root'
})
export class InterventionService {
  private apiUrl = `${environment.apiUrl}/interventions`;

  constructor(private http: HttpClient) {}

  /**
   * Récupère toutes les interventions
   */
  getAll(): Observable<Intervention[]> {
    return this.http.get<Intervention[]>(this.apiUrl);
  }

  /**
   * Récupère les interventions par statut
   */
  getByStatus(status: InterventionStatus): Observable<Intervention[]> {
    const params = new HttpParams().set('status', status);
    return this.http.get<Intervention[]>(this.apiUrl, { params });
  }

  /**
   * Récupère les interventions par type
   */
  getByType(type: InterventionType): Observable<Intervention[]> {
    const params = new HttpParams().set('type', type);
    return this.http.get<Intervention[]>(this.apiUrl, { params });
  }

  /**
   * Récupère une intervention par son ID
   */
  getById(id: number): Observable<Intervention> {
    return this.http.get<Intervention>(`${this.apiUrl}/${id}`);
  }

  /**
   * Crée une nouvelle intervention
   */
  create(intervention: Intervention): Observable<Intervention> {
    return this.http.post<Intervention>(this.apiUrl, intervention);
  }

  /**
   * Met à jour une intervention
   */
  update(id: number, intervention: Partial<Intervention>): Observable<Intervention> {
    return this.http.put<Intervention>(`${this.apiUrl}/${id}`, intervention);
  }

  /**
   * Supprime une intervention
   */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Change le statut d'une intervention
   */
  updateStatus(id: number, status: InterventionStatus): Observable<Intervention> {
    return this.http.patch<Intervention>(`${this.apiUrl}/${id}/status`, { status });
  }

  /**
   * Récupère les interventions d'un client
   */
  getByClient(clientId: number): Observable<Intervention[]> {
    const params = new HttpParams().set('clientId', clientId.toString());
    return this.http.get<Intervention[]>(this.apiUrl, { params });
  }

  /**
   * Récupère les interventions du calendrier
   */
  getCalendar(startDate?: string, endDate?: string): Observable<Intervention[]> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    return this.http.get<Intervention[]>(`${this.apiUrl}/calendar`, { params });
  }

  /**
   * Recherche d'interventions
   */
  search(query: string, filters?: {
    status?: InterventionStatus;
    type?: InterventionType;
    startDate?: string;
    endDate?: string;
  }): Observable<Intervention[]> {
    let params = new HttpParams().set('query', query);
    if (filters?.status) params = params.set('status', filters.status);
    if (filters?.type) params = params.set('type', filters.type);
    if (filters?.startDate) params = params.set('startDate', filters.startDate);
    if (filters?.endDate) params = params.set('endDate', filters.endDate);
    return this.http.get<Intervention[]>(`${this.apiUrl}/search`, { params });
  }

  /**
   * Récupère le nombre d'interventions par statut
   */
  getCountsByStatus(): Observable<Record<InterventionStatus, number>> {
    return this.http.get<Record<InterventionStatus, number>>(`${this.apiUrl}/counts-by-status`);
  }
}
