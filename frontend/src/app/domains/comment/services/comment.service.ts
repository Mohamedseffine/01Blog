import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

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
    return this.http.get<CommentListResponse>(`${this.apiUrl}/post/${postId}`, { params });
  }

  getCommentById(id: number): Observable<Comment> {
    return this.http.get<Comment>(`${this.apiUrl}/${id}`);
  }

  createComment(dto: CreateCommentDto): Observable<Comment> {
    return this.http.post<Comment>(this.apiUrl, dto);
  }

  updateComment(id: number, content: string): Observable<Comment> {
    return this.http.put<Comment>(`${this.apiUrl}/${id}`, { content });
  }

  deleteComment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
