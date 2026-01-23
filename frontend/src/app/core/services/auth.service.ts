import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';
import { environment } from '@env/environment';
import { CurrentUser } from '@domains/auth/models/auth.model';

export interface LoginPayload {
  usernameOrEmail: string;
  password: string;
}

export interface RegisterPayload {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  birthDate?: string; // ISO date
  gender: string;
  profileType: string;
  profilePicture?: File;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private base = `${environment.apiUrl}/auth`
  private accessToken: string | null = null
  private readonly tokenKey = 'auth_token';
  private currentUserSubject = new BehaviorSubject<CurrentUser | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadCurrentUser();
  }

  login(payload: LoginPayload): Observable<any> {
    return this.http.post(`${this.base}/login`, payload, { withCredentials: true });
  }

  register(payload: RegisterPayload): Observable<any> {
    const formData = new FormData();
    formData.append('firstName', payload.firstName);
    formData.append('lastName', payload.lastName);
    formData.append('username', payload.username);
    formData.append('email', payload.email);
    formData.append('password', payload.password);
    formData.append('confirmPassword', payload.confirmPassword);
    if (payload.birthDate) formData.append('birthDate', payload.birthDate);
    formData.append('gender', payload.gender);
    formData.append('profileType', payload.profileType);
    if (payload.profilePicture) formData.append('profilePicture', payload.profilePicture);
    return this.http.post(`${this.base}/register`, formData, { withCredentials: true });
  }

  refresh(): Observable<any> {
    return this.http.post(`${this.base}/refresh`, {}, { withCredentials: true });
  }

  getCurrentUser(): Observable<CurrentUser> {
    const token = this.getToken();
    const headers = token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : undefined;
    return this.http.get<any>(`${this.base}/me`, { headers }).pipe(
      map((res) => res?.data ?? res)
    );
  }

  getToken(): string | null {
    if (this.accessToken) return this.accessToken;
    const stored = localStorage.getItem(this.tokenKey);
    this.accessToken = stored;
    return stored;
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  setToken(token: string) {
    this.accessToken = token;
    localStorage.setItem(this.tokenKey, token);
    this.loadCurrentUser();
  }

  clearToken() {
    this.accessToken = null;
    localStorage.removeItem(this.tokenKey);
    this.currentUserSubject.next(null);
  }

  getCurrentUserSnapshot(): CurrentUser | null {
    return this.currentUserSubject.getValue();
  }

  logout() {
    this.clearToken();
    return this.http.post(`${this.base}/logout`, {}, { withCredentials: true });
  }

  private loadCurrentUser() {
    const token = this.getToken();
    if (!token) {
      this.currentUserSubject.next(null);
      return;
    }
    this.refreshCurrentUser().subscribe({
      error: () => this.currentUserSubject.next(null)
    });
  }

  refreshCurrentUser(): Observable<CurrentUser> {
    return this.getCurrentUser().pipe(
      tap((user) => this.currentUserSubject.next(user))
    );
  }
}
