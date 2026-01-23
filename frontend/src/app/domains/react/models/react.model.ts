export type ReactType = 'LIKE' | 'DISLIKE';

export interface ReactSummary {
  likeCount: number;
  dislikeCount: number;
  userReact?: ReactType | null;
}

export interface ReactRequest {
  reactType: ReactType;
}
