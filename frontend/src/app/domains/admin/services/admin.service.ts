import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '@env/environment';
import { AdminDashboard, AdminReport, AdminUser, ApiResponse, BanRequest, Page } from '../models/admin.model';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private base = `${environment.apiUrl}/admin`;
  private reportsBase = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient) {}

  getDashboard(): Observable<ApiResponse<AdminDashboard>> {
    return this.http.get<ApiResponse<AdminDashboard>>(`${this.base}/dashboard`);
  }

  getUsers(page: number = 0, size: number = 10): Observable<ApiResponse<Page<AdminUser>>> {
    return this.http.get<ApiResponse<Page<AdminUser>>>(`${this.base}/users`, {
      params: { page: page.toString(), size: size.toString() }
    });
  }

  banUser(userId: number, payload: BanRequest): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.base}/users/${userId}/ban`, payload);
  }

  unbanUser(userId: number): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.base}/users/${userId}/unban`, {});
  }

  getReports(page: number = 0, size: number = 10): Observable<ApiResponse<Page<AdminReport>>> {
    return this.http.get<ApiResponse<Page<AdminReport>>>(`${this.base}/reports`, {
      params: { page: page.toString(), size: size.toString() }
    });
  }

  resolveReport(reportId: number): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.reportsBase}/${reportId}/resolve`, {});
  }

  deletePost(postId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/posts/${postId}`);
  }
}
