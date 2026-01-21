export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface Page<T> {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface AdminDashboard {
  totalUsers: number;
  totalPosts: number;
  totalComments: number;
  totalReports: number;
  bannedUsers: number;
  pendingReports: number;
}

export interface AdminUser {
  id: number;
  username: string;
  email: string;
  role: string;
  banned: boolean;
  blocked: boolean;
  createdAt: string;
}

export interface AdminReport {
  id: number;
  contentType: string;
  contentId: number | null;
  reason: string;
  status: string;
  description: string;
  createdAt: string;
  resolvedAt: string | null;
  reporterUsername: string | null;
  reportedUsername: string | null;
}

export interface BanRequest {
  reason: string;
  isPermanent: boolean;
  durationDays?: number;
}
