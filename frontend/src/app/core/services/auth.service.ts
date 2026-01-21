import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';

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
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private base = `${environment.apiUrl}/auth`
  private accessToken: string | null = null

  constructor(private http: HttpClient) {}

  login(payload: LoginPayload): Observable<any> {
    return this.http.post(`${this.base}/login`, payload, { withCredentials: true });
  }

  register(payload: RegisterPayload): Observable<any> {
    return this.http.post(`${this.base}/register`, payload, { withCredentials: true });
  }

  refresh(): Observable<any> {
    return this.http.post(`${this.base}/refresh`, {}, { withCredentials: true });
  }

  getToken(): string | null {
    return this.accessToken;
  }

  setToken(token: string) {
    this.accessToken = token;
  }

  clearToken() {
    this.accessToken = null;
  }

  logout() {
    this.accessToken = null;
    return this.http.post(`${this.base}/logout`, {}, { withCredentials: true });
  }
}
