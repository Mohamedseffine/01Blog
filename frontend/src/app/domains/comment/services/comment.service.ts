import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import { environment } from '@env/environment';
import { Comment, CreateCommentDto, CommentListResponse } from '../models/comment.model';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private apiUrl = `${environment.apiUrl}/comments`;

  constructor(private http: HttpClient) { }

  getCommentsByPost(postId: number, page: number = 0, size: number = 20): Observable<CommentListResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.apiUrl}/post/${postId}`, { params }).pipe(
      map((res) => res?.data ?? res)
    );
  }

  getCommentById(id: number): Observable<Comment> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map((res) => res?.data ?? res)
    );
  }

  createComment(dto: CreateCommentDto): Observable<Comment> {
    return this.http.post<any>(this.apiUrl, dto).pipe(
      map((res) => res?.data ?? res)
    );
  }

  updateComment(id: number, content: string): Observable<Comment> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, { content }).pipe(
      map((res) => res?.data ?? res)
    );
  }

  deleteComment(id: number): Observable<void> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`).pipe(
      map((res) => res?.data ?? res)
    );
  }
}
