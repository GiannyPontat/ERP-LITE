import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { 
  CatalogItem, 
  CatalogCategory,
  CreateCatalogItemDto, 
  UpdateCatalogItemDto 
} from '../models/catalog.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class CatalogService {
  private readonly apiUrl = `${environment.apiUrl}/catalog-items`;

  constructor(private http: HttpClient) {}

  /**
   * Récupère tous les articles (avec pagination)
   */
  getAll(page?: number, size?: number, search?: string, category?: string): Observable<Page<CatalogItem>> {
    let params = new HttpParams();
    
    if (page !== undefined) params = params.set('page', page.toString());
    if (size !== undefined) params = params.set('size', size.toString());
    if (search) params = params.set('search', search);
    if (category && category !== 'ALL') params = params.set('category', category);
    
    return this.http.get<Page<CatalogItem>>(this.apiUrl, { params });
  }

  /**
   * Récupère un article par son ID
   */
  getById(id: number): Observable<CatalogItem> {
    return this.http.get<CatalogItem>(`${this.apiUrl}/${id}`);
  }

  /**
   * Crée un nouvel article
   */
  create(item: CreateCatalogItemDto): Observable<CatalogItem> {
    return this.http.post<CatalogItem>(this.apiUrl, item);
  }

  /**
   * Met à jour un article
   */
  update(id: number, item: UpdateCatalogItemDto): Observable<CatalogItem> {
    return this.http.put<CatalogItem>(`${this.apiUrl}/${id}`, item);
  }

  /**
   * Supprime un article
   */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Recherche d'articles
   */
  search(query: string, category?: CatalogCategory): Observable<CatalogItem[]> {
    let params = new HttpParams().set('query', query);
    if (category) params = params.set('category', category);
    return this.http.get<CatalogItem[]>(`${this.apiUrl}/search`, { params });
  }

  /**
   * Récupère les articles par catégorie
   */
  getByCategory(category: CatalogCategory): Observable<CatalogItem[]> {
    const params = new HttpParams().set('category', category);
    return this.http.get<CatalogItem[]>(this.apiUrl, { params });
  }

  /**
   * Récupère les articles avec stock faible
   */
  getLowStock(): Observable<CatalogItem[]> {
    return this.http.get<CatalogItem[]>(`${this.apiUrl}/low-stock`);
  }

  /**
   * Met à jour le stock d'un article
   */
  updateStock(id: number, quantity: number): Observable<CatalogItem> {
    return this.http.patch<CatalogItem>(`${this.apiUrl}/${id}/stock`, { quantity });
  }
}
