import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, map, throwError } from 'rxjs';

import { environment } from '@env/environment';
import { ReactRequest, ReactSummary } from '../models/react.model';
import { ErrorService } from '@core/services/error.service';

@Injectable({ providedIn: 'root' })
export class ReactService {
  private base = `${environment.apiUrl}/reacts`;

  constructor(private http: HttpClient, private errorService: ErrorService) {}

  private handleError<T>(message: string) {
    return (error: any) => {
      this.errorService.addWarning(message);
      return throwError(() => error);
    };
  }

  getPostSummary(postId: number): Observable<ReactSummary> {
    return this.http.get<any>(`${this.base}/posts/${postId}`).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<ReactSummary>('Unable to load post reactions.'))
    );
  }

  reactToPost(postId: number, reactType: ReactRequest['reactType']): Observable<ReactSummary> {
    return this.http.post<any>(`${this.base}/posts/${postId}`, { reactType }).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<ReactSummary>('Unable to update post reaction.'))
    );
  }

  removePostReact(postId: number): Observable<ReactSummary> {
    return this.http.delete<any>(`${this.base}/posts/${postId}`).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<ReactSummary>('Unable to remove post reaction.'))
    );
  }

  getCommentSummary(commentId: number): Observable<ReactSummary> {
    return this.http.get<any>(`${this.base}/comments/${commentId}`).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<ReactSummary>('Unable to load comment reactions.'))
    );
  }

  reactToComment(commentId: number, reactType: ReactRequest['reactType']): Observable<ReactSummary> {
    return this.http.post<any>(`${this.base}/comments/${commentId}`, { reactType }).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<ReactSummary>('Unable to update comment reaction.'))
    );
  }

  removeCommentReact(commentId: number): Observable<ReactSummary> {
    return this.http.delete<any>(`${this.base}/comments/${commentId}`).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<ReactSummary>('Unable to remove comment reaction.'))
    );
  }
}
