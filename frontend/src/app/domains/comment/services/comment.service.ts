import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError, map, of, throwError } from 'rxjs';

import { environment } from '@env/environment';
import { Comment, CreateCommentDto, CommentListResponse } from '../models/comment.model';
import { ErrorService } from '@core/services/error.service';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private apiUrl = `${environment.apiUrl}/comments`;

  constructor(private http: HttpClient, private errorService: ErrorService) { }

  private emptyList(page: number, size: number): CommentListResponse {
    return {
      content: [],
      number: page,
      size,
      totalPages: 0,
      totalElements: 0
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

  getCommentsByPost(postId: number, page: number = 0, size: number = 20): Observable<CommentListResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.apiUrl}/post/${postId}`, { params }).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<CommentListResponse>('Unable to load comments.', this.emptyList(page, size)))
    );
  }

  getCommentById(id: number): Observable<Comment> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<Comment>('Unable to load comment.'))
    );
  }

  createComment(dto: CreateCommentDto): Observable<Comment> {
    return this.http.post<any>(this.apiUrl, dto).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<Comment>('Unable to create comment.'))
    );
  }

  updateComment(id: number, content: string): Observable<Comment> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, { content }).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<Comment>('Unable to update comment.'))
    );
  }

  deleteComment(id: number): Observable<void> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<void>('Unable to delete comment.'))
    );
  }
}
