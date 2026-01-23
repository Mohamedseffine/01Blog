import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '@env/environment';
import { AdminComment, AdminDashboard, AdminPost, AdminReport, AdminUser, ApiResponse, BanRequest, Page } from '../models/admin.model';

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

  getPosts(page: number = 0, size: number = 10, filters?: {
    sortDir?: string;
    visibility?: string;
    hidden?: boolean;
    creatorId?: number;
    creatorUsername?: string;
  }): Observable<ApiResponse<Page<AdminPost>>> {
    const params = this.buildParams({
      page,
      size,
      sortDir: filters?.sortDir,
      visibility: filters?.visibility,
      hidden: filters?.hidden,
      creatorId: filters?.creatorId,
      creatorUsername: filters?.creatorUsername
    });
    return this.http.get<ApiResponse<Page<AdminPost>>>(`${this.base}/posts`, { params });
  }

  getComments(page: number = 0, size: number = 10, filters?: {
    sortDir?: string;
    hidden?: boolean;
    postId?: number;
    creatorId?: number;
    creatorUsername?: string;
  }): Observable<ApiResponse<Page<AdminComment>>> {
    const params = this.buildParams({
      page,
      size,
      sortDir: filters?.sortDir,
      hidden: filters?.hidden,
      postId: filters?.postId,
      creatorId: filters?.creatorId,
      creatorUsername: filters?.creatorUsername
    });
    return this.http.get<ApiResponse<Page<AdminComment>>>(`${this.base}/comments`, { params });
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

  hidePost(postId: number): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.base}/posts/${postId}/hide`, {});
  }

  unhidePost(postId: number): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.base}/posts/${postId}/unhide`, {});
  }

  private buildParams(values: Record<string, string | number | boolean | undefined | null>): HttpParams {
    let params = new HttpParams();
    Object.entries(values).forEach(([key, value]) => {
      if (value === undefined || value === null || value === '') return;
      params = params.set(key, String(value));
    });
    return params;
  }
}
