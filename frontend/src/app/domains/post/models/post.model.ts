/**
 * Post Domain Model
 * Represents the core post entity in the blogging system
 */
export interface Post {
  id: number;
  creatorUsername: string;
  postTitle: string;
  postContent: string;
  postSubject: string[];
  postVisibility: PostVisibility;
  medias?: string[];
}

export interface CreatePostDto {
  postTitle: string;
  postContent: string;
  postSubject: string[];
  postVisibility: PostVisibility;
  multipartFiles?: File[];
}

export interface UpdatePostDto {
  postTitle?: string;
  postContent?: string;
  postSubject?: string[];
  postVisibility?: PostVisibility;
  multipartFiles?: File[];
}

export enum PostVisibility {
  PUBLIC = 'PUBLIC',
  PRIVATE = 'PRIVATE',
  CLOSEFRIEND = 'CLOSEFRIEND'
}

export interface PostListResponse {
  content: Post[];
  number: number;
  size: number;
  totalPages: number;
  totalElements: number;
}
