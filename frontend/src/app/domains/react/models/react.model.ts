/**
 * React (Reaction) Domain Model
 */
export enum ReactionType {
  LIKE = 'LIKE',
  LOVE = 'LOVE',
  HAHA = 'HAHA',
  WOW = 'WOW',
  SAD = 'SAD',
  ANGRY = 'ANGRY'
}

export interface React {
  id: number;
  type: ReactionType;
  contentId: number;
  contentType: string; // 'POST' or 'COMMENT'
  creatorId: number;
  createdAt: Date;
}

export interface ReactDto {
  type: ReactionType;
  contentId: number;
  contentType: string;
}
