import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, tap, throwError } from 'rxjs';

/**
 * HTTP Client Wrapper Service
 * Provides logging, error handling, and consistent request/response patterns
 * Wraps Angular's HttpClient with additional features
 */
@Injectable({
  providedIn: 'root'
})
export class HttpClientService {
  
  constructor(private http: HttpClient) {}

  /**
   * GET request with logging
   */
  get<T>(url: string, options?: any): Observable<T> {
    console.debug(`[HTTP GET] ${url}`);
    return this.http.get<T>(url, options).pipe(
      tap((response) => console.debug(`[HTTP GET SUCCESS] ${url}`, response)),
      catchError((error) => this.handleError(error, 'GET', url))
    ) as Observable<T>;
  }

  /**
   * POST request with logging
   */
  post<T>(url: string, body: any, options?: any): Observable<T> {
    console.debug(`[HTTP POST] ${url}`, body);
    return this.http.post<T>(url, body, options).pipe(
      tap((response) => console.debug(`[HTTP POST SUCCESS] ${url}`, response)),
      catchError((error) => this.handleError(error, 'POST', url))
    ) as Observable<T>;
  }

  /**
   * PUT request with logging
   */
  put<T>(url: string, body: any, options?: any): Observable<T> {
    console.debug(`[HTTP PUT] ${url}`, body);
    return this.http.put<T>(url, body, options).pipe(
      tap((response) => console.debug(`[HTTP PUT SUCCESS] ${url}`, response)),
      catchError((error) => this.handleError(error, 'PUT', url))
    ) as Observable<T>;
  }

  /**
   * PATCH request with logging
   */
  patch<T>(url: string, body: any, options?: any): Observable<T> {
    console.debug(`[HTTP PATCH] ${url}`, body);
    return this.http.patch<T>(url, body, options).pipe(
      tap((response) => console.debug(`[HTTP PATCH SUCCESS] ${url}`, response)),
      catchError((error) => this.handleError(error, 'PATCH', url))
    ) as Observable<T>;
  }

  /**
   * DELETE request with logging
   */
  delete<T>(url: string, options?: any): Observable<T> {
    console.debug(`[HTTP DELETE] ${url}`);
    return this.http.delete<T>(url, options).pipe(
      tap((response) => console.debug(`[HTTP DELETE SUCCESS] ${url}`, response)),
      catchError((error) => this.handleError(error, 'DELETE', url))
    ) as Observable<T>;
  }

  /**
   * Handle HTTP errors with logging
   */
  private handleError(error: HttpErrorResponse, method: string, url: string): Observable<never> {
    const errorMessage = error.error?.message || error.statusText || 'Unknown error';
    console.debug(`[HTTP ${method} ERROR] ${url}`, {
      status: error.status,
      statusText: error.statusText,
      message: errorMessage,
      error: error.error
    });
    
    return throwError(() => error);
  }
}
