/**
 * Notification Domain Model
 */
export interface Notification {
  id: number;
  message: string;
  type: NotificationType;
  isRead: boolean;
  createdAt: Date;
  contentId?: number;
  actor?: {
    id: number;
    username: string;
  };
}

export enum NotificationType {
  COMMENT = 'COMMENT',
  REACTION = 'REACTION',
  FOLLOW = 'FOLLOW',
  MENTION = 'MENTION',
  SYSTEM = 'SYSTEM'
}

export interface NotificationResponse {
  content: Notification[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    totalPages: number;
    totalElements: number;
  };
  unreadCount: number;
}
