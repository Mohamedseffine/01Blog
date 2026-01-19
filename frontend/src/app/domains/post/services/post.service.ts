import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

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
    return this.http.get<PostListResponse>(this.apiUrl, { params });
  }

  /**
   * Get a single post by ID
   */
  getPostById(id: number): Observable<Post> {
    return this.http.get<Post>(`${this.apiUrl}/${id}`);
  }

  /**
   * Create a new post
   */
  createPost(dto: CreatePostDto): Observable<Post> {
    const formData = new FormData();
    formData.append('title', dto.title);
    formData.append('content', dto.content);
    if (dto.description) formData.append('description', dto.description);
    if (dto.mediaFiles) {
      dto.mediaFiles.forEach(file => formData.append('mediaFiles', file));
    }
    return this.http.post<Post>(this.apiUrl, formData);
  }

  /**
   * Update an existing post
   */
  updatePost(id: number, dto: UpdatePostDto): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrl}/${id}`, dto);
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
    return this.http.get<PostListResponse>(`${this.apiUrl}/user/${userId}`, { params });
  }
}
