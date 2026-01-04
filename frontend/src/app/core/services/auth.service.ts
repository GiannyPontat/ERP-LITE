import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, RegisterRequest, AuthResponse, MessageResponse } from '../models/auth.model';
import { User } from '../models/user.model';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$: Observable<User | null> = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router,
    private storageService: StorageService
  ) {
    // Restore user from storage if available
    const user = this.storageService.getUser();
    if (user && this.isAuthenticated()) {
      this.currentUserSubject.next(user);
    } else {
      this.currentUserSubject.next(null);
    }
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        this.storageService.setToken(response.accessToken);
        this.storageService.setRefreshToken(response.refreshToken);
        this.storageService.setUser(response.user);
        this.currentUserSubject.next(response.user);
      }),
      catchError(error => {
        console.error('Login error:', error);
        return throwError(() => error);
      })
    );
  }

  register(data: RegisterRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.apiUrl}/register`, data);
  }

  logout(): void {
    const token = this.getToken();
    if (token) {
      // Optionally call logout endpoint (requires auth, so might fail if token expired)
      this.http.post(`${this.apiUrl}/logout`, {}).subscribe({
        next: () => this.clearAuth(),
        error: () => this.clearAuth() // Clear anyway if logout fails
      });
    } else {
      this.clearAuth();
    }
  }

  private clearAuth(): void {
    this.storageService.clear();
    this.currentUserSubject.next(null);
    this.router.navigate(['/auth/login']);
  }

  getToken(): string | null {
    return this.storageService.getToken();
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

    // Check if token is expired (basic check)
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const exp = payload.exp * 1000; // Convert to milliseconds
      return Date.now() < exp;
    } catch {
      return false;
    }
  }

  refreshToken(): Observable<AuthResponse> {
    const refreshToken = this.storageService.getRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }

    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, { refreshToken }).pipe(
      tap(response => {
        this.storageService.setToken(response.accessToken);
        this.storageService.setRefreshToken(response.refreshToken);
        this.storageService.setUser(response.user);
        this.currentUserSubject.next(response.user);
      }),
      catchError(error => {
        console.error('Refresh token error:', error);
        this.clearAuth();
        return throwError(() => error);
      })
    );
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }
}

