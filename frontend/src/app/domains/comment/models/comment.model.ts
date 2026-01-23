/**
 * Comment Domain Model
 */
export interface Comment {
  id: number;
  content: string;
  postId: number;
  parentId?: number;
  hidden?: boolean;
  creatorId?: number;
  creatorUsername?: string;
  createdAt: string;
  modifiedAt?: string;
  modified?: boolean;
  children?: Comment[];
}

export interface CreateCommentDto {
  content: string;
  postId: number;
  parentId?: number;
}

export interface CommentListResponse {
  content: Comment[];
  number: number;
  size: number;
  totalPages: number;
  totalElements: number;
}
