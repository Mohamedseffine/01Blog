import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';

import { environment } from '@env/environment';
import { LoginRequest, RegisterRequest, AuthResponse, CurrentUser } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<CurrentUser | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadCurrentUser();
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap((response: AuthResponse) => this.storeToken(response.token))
    );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request).pipe(
      tap((response: AuthResponse) => this.storeToken(response.token))
    );
  }

  logout(): void {
    this.clearToken();
    this.currentUserSubject.next(null);
  }

  getCurrentUser(): Observable<CurrentUser> {
    return this.http.get<CurrentUser>(`${this.apiUrl}/me`);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  private storeToken(token: string): void {
    localStorage.setItem('auth_token', token);
    this.loadCurrentUser();
  }

  private clearToken(): void {
    localStorage.removeItem('auth_token');
  }

  private loadCurrentUser(): void {
    const token = this.getToken();
    if (token) {
      this.getCurrentUser().subscribe(
        (user: CurrentUser) => this.currentUserSubject.next(user),
        () => this.clearToken()
      );
    }
  }
}
