import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import { environment } from '@env/environment';
import { Post, CreatePostDto, UpdatePostDto, PostListResponse } from '../models/post.model';

/**
 * Post Domain Service
 * Handles all post-related API communications and business logic
 */
@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiUrl = `${environment.apiUrl}/posts`;

  constructor(private http: HttpClient) { }

  /**
   * Get all posts with pagination
   */
  getPosts(page: number = 0, size: number = 10): Observable<PostListResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(this.apiUrl, { params }).pipe(
      map((res) => res?.data ?? res)
    );
  }

  /**
   * Get a single post by ID
   */
  getPostById(id: number): Observable<Post> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map((res) => res?.data ?? res)
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
      map((res) => res?.data ?? res)
    );
  }

  /**
   * Update an existing post
   */
  updatePost(id: number, dto: UpdatePostDto): Observable<Post> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, dto).pipe(
      map((res) => res?.data ?? res)
    );
  }

  /**
   * Delete a post
   */
  deletePost(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Get user's posts
   */
  getUserPosts(userId: number, page: number = 0, size: number = 10): Observable<PostListResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.apiUrl}/user/${userId}`, { params }).pipe(
      map((res) => res?.data ?? res)
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
      }))
    );
  }
}
