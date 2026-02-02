import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError, map, of, throwError } from 'rxjs';

import { environment } from '@env/environment';
import { Post, CreatePostDto, UpdatePostDto, PostListResponse } from '../models/post.model';
import { ErrorService } from '@core/services/error.service';

/**
 * Post Domain Service
 * Handles all post-related API communications and business logic
 */
@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiUrl = `${environment.apiUrl}/posts`;

  constructor(private http: HttpClient, private errorService: ErrorService) { }

  private emptyList(page: number, size: number): PostListResponse {
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

  /**
   * Get all posts with pagination
   */
  getPosts(page: number = 0, size: number = 10): Observable<PostListResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(this.apiUrl, { params }).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<PostListResponse>('Unable to load posts.', this.emptyList(page, size)))
    );
  }

  /**
   * Get a single post by ID
   */
  getPostById(id: number): Observable<Post> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<Post>('Unable to load post.'))
    );
  }

  /**
   * Create a new post
   */
  createPost(dto: CreatePostDto): Observable<Post> {
    const formData = new FormData();
    formData.append('postTitle', dto.postTitle);
    formData.append('postContent', dto.postContent);
    dto.postSubject.forEach(subject => formData.append('postSubject', subject));
    formData.append('postVisibility', dto.postVisibility);
    if (dto.multipartFiles) {
      
      dto.multipartFiles.forEach(file => formData.append('multipartFiles', file));
    }
    return this.http.post<any>(`${this.apiUrl}/create`, formData).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<Post>('Unable to create post.'))
    );
  }

  /**
   * Update an existing post
   */
  updatePost(id: number, dto: UpdatePostDto): Observable<Post> {
    const formData = new FormData();
    if (dto.postTitle !== undefined) formData.append('postTitle', dto.postTitle);
    if (dto.postContent !== undefined) formData.append('postContent', dto.postContent);
    if (dto.postVisibility !== undefined) formData.append('postVisibility', dto.postVisibility);
    if (dto.postSubject) {
      dto.postSubject.forEach(subject => formData.append('postSubject', subject));
    }
    if (dto.multipartFiles) {
      dto.multipartFiles.forEach(file => formData.append('multipartFiles', file));
    }

    return this.http.put<any>(`${this.apiUrl}/${id}`, formData).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<Post>('Unable to update post.'))
    );
  }

  /**
   * Delete a post
   */
  deletePost(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError<void>('Unable to delete post.'))
    );
  }

  /**
   * Get user's posts
   */
  getUserPosts(userId: number, page: number = 0, size: number = 10): Observable<PostListResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.apiUrl}/user/${userId}`, { params }).pipe(
      map((res) => res?.data ?? res),
      catchError(this.handleError<PostListResponse>('Unable to load user posts.', this.emptyList(page, size)))
    );
  }

  getPostMedia(postId: number, index: number): Observable<{ url: string; type: string }> {
    return this.http.get(`${this.apiUrl}/${postId}/media/${index}`, {
      observe: 'response',
      responseType: 'blob'
    }).pipe(
      map((res) => ({
        url: URL.createObjectURL(res.body as Blob),
        type: res.headers.get('Content-Type') || 'application/octet-stream'
      })),
      catchError(this.handleError<{ url: string; type: string }>('Unable to load post media.'))
    );
  }
}
