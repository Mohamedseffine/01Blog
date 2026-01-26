/**
 * Notification Domain Model
 */
export interface Notification {
  id: number;
  message: string;
  type: NotificationType | string;
  isRead: boolean;
  createdAt: string;
  contentId?: number;
  actor?: {
    id: number;
    username: string;
  };
}

export enum NotificationType {
  REACT = 'REACT',
  COMMENT = 'COMMENT',
  FOLLOW = 'FOLLOW',
  MENTION = 'MENTION',
  POST = 'POST',
  POST_APPROVED = 'POST_APPROVED',
  POST_REJECTED = 'POST_REJECTED',
  REPORT = 'REPORT',
  SYSTEM = 'SYSTEM'
}

export interface NotificationResponse {
  content: Notification[];
  number: number;
  size: number;
  totalPages: number;
  totalElements: number;
}
