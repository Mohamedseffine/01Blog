/**
 * Comment Domain Model
 */
export interface Comment {
  id: number;
  content: string;
  postId: number;
  creatorId: number;
  createdAt: Date;
  updatedAt: Date;
}

export interface CreateCommentDto {
  content: string;
  postId: number;
}

export interface CommentListResponse {
  content: Comment[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    totalPages: number;
    totalElements: number;
  };
}
