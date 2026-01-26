import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError, of, throwError } from 'rxjs';

import { environment } from '@env/environment';
import { AdminComment, AdminDashboard, AdminPost, AdminReport, AdminUser, ApiResponse, BanRequest, Page } from '../models/admin.model';
import { ErrorService } from '@core/services/error.service';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private base = `${environment.apiUrl}/admin`;
  private reportsBase = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient, private errorService: ErrorService) {}

  private emptyPage<T>(size: number): Page<T> {
    return {
      content: [],
      number: 0,
      size,
      totalElements: 0,
      totalPages: 0,
      first: true,
      last: true
    };
  }

  private emptyDashboard(): AdminDashboard {
    return {
      totalUsers: 0,
      totalPosts: 0,
      totalComments: 0,
      totalReports: 0,
      bannedUsers: 0,
      pendingReports: 0
    };
  }

  private handleError<T>(message: string, fallback?: T) {
    return (error: any) => {
      this.errorService.addError(message);
      if (fallback !== undefined) {
        return of(fallback as T);
      }
      return throwError(() => error);
    };
  }

  getDashboard(): Observable<ApiResponse<AdminDashboard>> {
    return this.http.get<ApiResponse<AdminDashboard>>(`${this.base}/dashboard`).pipe(
      catchError(this.handleError<ApiResponse<AdminDashboard>>('Unable to load dashboard.', {
        success: false,
        message: 'Unable to load dashboard.',
        data: this.emptyDashboard()
      }))
    );
  }

  getUsers(page: number = 0, size: number = 10): Observable<ApiResponse<Page<AdminUser>>> {
    return this.http.get<ApiResponse<Page<AdminUser>>>(`${this.base}/users`, {
      params: { page: page.toString(), size: size.toString() }
    }).pipe(
      catchError(this.handleError<ApiResponse<Page<AdminUser>>>('Unable to load users.', {
        success: false,
        message: 'Unable to load users.',
        data: this.emptyPage<AdminUser>(size)
      }))
    );
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
    return this.http.get<ApiResponse<Page<AdminPost>>>(`${this.base}/posts`, { params }).pipe(
      catchError(this.handleError<ApiResponse<Page<AdminPost>>>('Unable to load posts.', {
        success: false,
        message: 'Unable to load posts.',
        data: this.emptyPage<AdminPost>(size)
      }))
    );
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
    return this.http.get<ApiResponse<Page<AdminComment>>>(`${this.base}/comments`, { params }).pipe(
      catchError(this.handleError<ApiResponse<Page<AdminComment>>>('Unable to load comments.', {
        success: false,
        message: 'Unable to load comments.',
        data: this.emptyPage<AdminComment>(size)
      }))
    );
  }

  banUser(userId: number, payload: BanRequest): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.base}/users/${userId}/ban`, payload).pipe(
      catchError(this.handleError<ApiResponse<void>>('Unable to ban user.'))
    );
  }

  unbanUser(userId: number): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.base}/users/${userId}/unban`, {}).pipe(
      catchError(this.handleError<ApiResponse<void>>('Unable to unban user.'))
    );
  }

  getReports(page: number = 0, size: number = 10): Observable<ApiResponse<Page<AdminReport>>> {
    return this.http.get<ApiResponse<Page<AdminReport>>>(`${this.base}/reports`, {
      params: { page: page.toString(), size: size.toString() }
    }).pipe(
      catchError(this.handleError<ApiResponse<Page<AdminReport>>>('Unable to load reports.', {
        success: false,
        message: 'Unable to load reports.',
        data: this.emptyPage<AdminReport>(size)
      }))
    );
  }

  resolveReport(reportId: number): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.reportsBase}/${reportId}/resolve`, {}).pipe(
      catchError(this.handleError<ApiResponse<void>>('Unable to resolve report.'))
    );
  }

  deletePost(postId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/posts/${postId}`).pipe(
      catchError(this.handleError<ApiResponse<void>>('Unable to delete post.'))
    );
  }

  hideComment(commentId: number): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.base}/comments/${commentId}/hide`, {}).pipe(
      catchError(this.handleError<ApiResponse<void>>('Unable to hide comment.'))
    );
  }

  unhideComment(commentId: number): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.base}/comments/${commentId}/unhide`, {}).pipe(
      catchError(this.handleError<ApiResponse<void>>('Unable to unhide comment.'))
    );
  }

  deleteComment(commentId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/comments/${commentId}`).pipe(
      catchError(this.handleError<ApiResponse<void>>('Unable to delete comment.'))
    );
  }

  hidePost(postId: number): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.base}/posts/${postId}/hide`, {}).pipe(
      catchError(this.handleError<ApiResponse<void>>('Unable to hide post.'))
    );
  }

  unhidePost(postId: number): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.base}/posts/${postId}/unhide`, {}).pipe(
      catchError(this.handleError<ApiResponse<void>>('Unable to unhide post.'))
    );
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
