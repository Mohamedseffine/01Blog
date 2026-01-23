export type ReportReason =
  | 'SPAM'
  | 'HARASSMENT'
  | 'HATE_SPEECH'
  | 'VIOLENCE'
  | 'SEXUAL_CONTENT'
  | 'MISINFORMATION'
  | 'COPYRIGHT'
  | 'OTHER';

export interface CreateReportDto {
  postId?: number;
  commentId?: number;
  reportedUserId?: number;
  reason: ReportReason;
  description?: string;
}
