/**
 * Post Domain Model
 * Represents the core post entity in the blogging system
 */
export interface Post {
  id: number;
  title: string;
  content: string;
  description?: string;
  createdAt: Date;
  updatedAt: Date;
  creatorId: number;
  mediaUrls?: string[];
  status: PostStatus;
}

export interface CreatePostDto {
  title: string;
  content: string;
  description?: string;
  mediaFiles?: File[];
}

export interface UpdatePostDto {
  title?: string;
  content?: string;
  description?: string;
}

export enum PostStatus {
  PUBLISHED = 'PUBLISHED',
  DRAFT = 'DRAFT',
  ARCHIVED = 'ARCHIVED'
}

export interface PostListResponse {
  content: Post[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    totalPages: number;
    totalElements: number;
  };
}
