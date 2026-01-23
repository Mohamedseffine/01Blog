import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import { environment } from '@env/environment';
import { ReactRequest, ReactSummary } from '../models/react.model';

@Injectable({ providedIn: 'root' })
export class ReactService {
  private base = `${environment.apiUrl}/reacts`;

  constructor(private http: HttpClient) {}

  getPostSummary(postId: number): Observable<ReactSummary> {
    return this.http.get<any>(`${this.base}/posts/${postId}`).pipe(
      map((res) => res?.data ?? res)
    );
  }

  reactToPost(postId: number, reactType: ReactRequest['reactType']): Observable<ReactSummary> {
    return this.http.post<any>(`${this.base}/posts/${postId}`, { reactType }).pipe(
      map((res) => res?.data ?? res)
    );
  }

  removePostReact(postId: number): Observable<ReactSummary> {
    return this.http.delete<any>(`${this.base}/posts/${postId}`).pipe(
      map((res) => res?.data ?? res)
    );
  }

  getCommentSummary(commentId: number): Observable<ReactSummary> {
    return this.http.get<any>(`${this.base}/comments/${commentId}`).pipe(
      map((res) => res?.data ?? res)
    );
  }

  reactToComment(commentId: number, reactType: ReactRequest['reactType']): Observable<ReactSummary> {
    return this.http.post<any>(`${this.base}/comments/${commentId}`, { reactType }).pipe(
      map((res) => res?.data ?? res)
    );
  }

  removeCommentReact(commentId: number): Observable<ReactSummary> {
    return this.http.delete<any>(`${this.base}/comments/${commentId}`).pipe(
      map((res) => res?.data ?? res)
    );
  }
}
