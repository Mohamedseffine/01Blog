import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { catchError, map, of, switchMap, forkJoin } from 'rxjs';
import { CommentService } from '@domains/comment/services/comment.service';
import { ReportService } from '@domains/report/services/report.service';
import { ReportReason } from '@domains/report/models/report.model';
import { AuthService } from '@core/services/auth.service';
import { Comment } from '@domains/comment/models/comment.model';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="container py-4">
      <ng-container *ngIf="post$ | async as post; else loading">
        <ng-container *ngIf="post; else errorTpl">
          <div class="mb-3">
            <a routerLink="/posts/list" class="btn btn-link p-0">&larr; Back to posts</a>
          </div>
          <div class="d-flex justify-content-between align-items-start mb-2">
            <h1 class="mb-0">{{ post.postTitle }}</h1>
            <div class="d-flex gap-2" *ngIf="canManagePost(post)">
              <a class="btn btn-sm btn-outline-primary" [routerLink]="['/posts', post.id, 'edit']">Edit</a>
              <button class="btn btn-sm btn-outline-danger" (click)="deletePost(post.id)">Delete</button>
            </div>
          </div>
          <div class="text-muted mb-3">by {{ post.creatorUsername }}</div>

          <div class="mb-4" *ngIf="canReportPost()">
            <button class="btn btn-sm btn-outline-danger" type="button" (click)="toggleReport()">
              {{ reportOpen() ? 'Cancel Report' : 'Report Post' }}
            </button>
            <div class="card mt-3" *ngIf="reportOpen()">
              <div class="card-body">
                <form (submit)="submitReport($event, post.id)">
                  <div class="mb-3">
                    <label class="form-label">Reason</label>
                    <select class="form-select" [ngModel]="reportReason()" (ngModelChange)="reportReason.set($event)" name="reason" required>
                      <option *ngFor="let reason of reportReasons" [ngValue]="reason">
                        {{ reason }}
                      </option>
                    </select>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Details (optional)</label>
                    <textarea class="form-control" rows="3" [ngModel]="reportDescription()" (ngModelChange)="reportDescription.set($event)" name="description"></textarea>
                  </div>
                  <div *ngIf="reportError()" class="alert alert-danger">{{ reportError() }}</div>
                  <div *ngIf="reportSuccess()" class="alert alert-success">{{ reportSuccess() }}</div>
                  <button class="btn btn-sm btn-danger" [disabled]="reportLoading()">
                    {{ reportLoading() ? 'Submitting...' : 'Submit Report' }}
                  </button>
                </form>
              </div>
            </div>
          </div>
          <div class="mb-4">
            <span class="badge bg-secondary me-2" *ngFor="let subject of post.postSubject">
              {{ subject }}
            </span>
          </div>
          <div class="lead" style="white-space: pre-wrap;">{{ post.postContent }}</div>

          <div class="mt-5">
            <h4 class="mb-3">Comments</h4>
            <form class="mb-3" (submit)="submitComment($event, post.id)">
              <textarea
                class="form-control"
                rows="3"
                placeholder="Write a comment..."
                [ngModel]="commentText()"
                (ngModelChange)="commentText.set($event)"
                name="comment"
                required
              ></textarea>
              <div class="d-flex justify-content-end mt-2">
                <button class="btn btn-sm btn-primary" [disabled]="commentLoading()">
                  {{ commentLoading() ? 'Posting...' : 'Post Comment' }}
                </button>
              </div>
            </form>

            <div *ngIf="commentsLoading()" class="text-muted">Loading comments...</div>
            <div *ngIf="commentsError()" class="text-danger">{{ commentsError() }}</div>

            <div *ngIf="!commentsLoading() && !comments().length" class="text-muted">
              No comments yet.
            </div>

            <div class="list-group">
              <div class="list-group-item" *ngFor="let c of comments()">
                <div class="small text-muted">{{ c.createdAt | date:'short' }}</div>
                <div>{{ c.content }}</div>
                <div class="mt-2 d-flex gap-2" *ngIf="canManageComment(c)">
                  <button class="btn btn-sm btn-outline-primary" (click)="startEditComment(c)">Edit</button>
                  <button class="btn btn-sm btn-outline-danger" (click)="deleteComment(c)">Delete</button>
                </div>
                <div class="mt-2" *ngIf="editingCommentId() === c.id">
                  <textarea
                    class="form-control"
                    rows="2"
                    [ngModel]="editCommentText()"
                    (ngModelChange)="editCommentText.set($event)"
                    name="editComment"
                  ></textarea>
                  <div class="d-flex justify-content-end gap-2 mt-2">
                    <button class="btn btn-sm btn-outline-secondary" (click)="cancelEdit()">Cancel</button>
                    <button class="btn btn-sm btn-primary" (click)="saveEditComment(c)">Save</button>
                  </div>
                </div>
                <div class="ms-3 mt-2" *ngFor="let child of c.children || []">
                  <div class="small text-muted">{{ child.createdAt | date:'short' }}</div>
                  <div>{{ child.content }}</div>
                </div>
              </div>
            </div>
          </div>

          <div class="mt-4" *ngIf="media$ | async as media">
            <div class="row g-3">
              <div class="col-md-6" *ngFor="let item of media">
                <ng-container *ngIf="item.type.startsWith('video'); else imageTpl">
                  <video class="w-100 rounded shadow-sm" controls [src]="item.url"></video>
                </ng-container>
                <ng-template #imageTpl>
                  <img class="img-fluid rounded shadow-sm" [src]="item.url" alt="Post media" />
                </ng-template>
              </div>
            </div>
          </div>
        </ng-container>
      </ng-container>

      <ng-template #loading>
        <p class="text-muted">Loading post...</p>
      </ng-template>

      <ng-template #errorTpl>
        <p class="text-danger">{{ error() || 'Post not found.' }}</p>
      </ng-template>
    </div>
  `,
})
export class PostDetailComponent {
  error = signal('');
  comments = signal<Comment[]>([]);
  commentsLoading = signal(false);
  commentsError = signal('');
  commentText = signal('');
  commentLoading = signal(false);
  editingCommentId = signal<number | null>(null);
  editCommentText = signal('');
  reportOpen = signal(false);
  reportReason = signal<ReportReason>('SPAM');
  reportDescription = signal('');
  reportError = signal('');
  reportSuccess = signal('');
  reportLoading = signal(false);
  reportReasons: ReportReason[] = [
    'SPAM',
    'HARASSMENT',
    'HATE_SPEECH',
    'VIOLENCE',
    'SEXUAL_CONTENT',
    'MISINFORMATION',
    'COPYRIGHT',
    'OTHER'
  ];

  post$ = this.route.paramMap.pipe(
    map((params) => Number(params.get('id'))),
    switchMap((id) => {
      if (!id || Number.isNaN(id)) {
        this.error.set('Invalid post id.');
        return of(null);
      }
      return this.postService.getPostById(id).pipe(
        catchError(() => {
          this.error.set('Unable to load post.');
          return of(null);
        })
      );
    })
  );
  media$ = this.post$.pipe(
    switchMap((post) => {
      if (!post?.id || !post.medias?.length) return of([]);
      const requests = post.medias.map((_, index) =>
        this.postService.getPostMedia(post.id, index).pipe(
          catchError(() => of(null))
        )
      );
      return forkJoin(requests).pipe(
        map((items) => items.filter((item) => item !== null) as { url: string; type: string }[])
      );
    })
  );

  constructor(
    private route: ActivatedRoute,
    private postService: PostService,
    private commentService: CommentService,
    private authService: AuthService,
    private router: Router,
    private reportService: ReportService
  ) {
    this.post$.subscribe((post) => {
      if (post?.id) {
        this.loadComments(post.id);
      }
    });
  }

  loadComments(postId: number) {
    this.commentsLoading.set(true);
    this.commentsError.set('');
    this.commentService.getCommentsByPost(postId).subscribe({
      next: (res) => {
        this.comments.set(res?.content ?? []);
        this.commentsLoading.set(false);
      },
      error: () => {
        this.commentsLoading.set(false);
        this.commentsError.set('Unable to load comments.');
      }
    });
  }

  submitComment(event: Event, postId: number) {
    event.preventDefault();
    const text = this.commentText().trim();
    if (!text) return;
    this.commentLoading.set(true);
    this.commentService.createComment({ content: text, postId }).subscribe({
      next: (comment) => {
        this.commentText.set('');
        this.commentLoading.set(false);
        this.comments.set([comment, ...this.comments()]);
      },
      error: () => {
        this.commentLoading.set(false);
        this.commentsError.set('Unable to create comment.');
      }
    });
  }

  canManagePost(post: any) {
    const user = this.authService.getCurrentUserSnapshot();
    if (!user) return false;
    const isAdmin = user.roles?.includes('ADMIN') || user.roles?.includes('ROLE_ADMIN');
    return isAdmin || user.username === post.creatorUsername;
  }

  canManageComment(comment: Comment) {
    const user = this.authService.getCurrentUserSnapshot();
    if (!user) return false;
    const isAdmin = user.roles?.includes('ADMIN') || user.roles?.includes('ROLE_ADMIN');
    return isAdmin || user.username === comment.creatorUsername;
  }

  canReportPost() {
    const user = this.authService.getCurrentUserSnapshot();
    if (!user) return false;
    return !(user.roles?.includes('ADMIN') || user.roles?.includes('ROLE_ADMIN'));
  }

  deletePost(postId: number) {
    if (!confirm('Delete this post?')) return;
    this.postService.deletePost(postId).subscribe({
      next: () => this.router.navigate(['/posts/list']),
      error: () => {
        this.error.set('Unable to delete post.');
      }
    });
  }

  startEditComment(comment: Comment) {
    this.editingCommentId.set(comment.id);
    this.editCommentText.set(comment.content);
  }

  cancelEdit() {
    this.editingCommentId.set(null);
    this.editCommentText.set('');
  }

  saveEditComment(comment: Comment) {
    const text = this.editCommentText().trim();
    if (!text) return;
    this.commentService.updateComment(comment.id, text).subscribe({
      next: (updated) => {
        this.comments.set(this.comments().map(c => c.id === comment.id ? updated : c));
        this.cancelEdit();
      },
      error: () => {
        this.commentsError.set('Unable to update comment.');
      }
    });
  }

  deleteComment(comment: Comment) {
    if (!confirm('Delete this comment?')) return;
    this.commentService.deleteComment(comment.id).subscribe({
      next: () => {
        this.comments.set(this.comments().filter(c => c.id !== comment.id));
      },
      error: () => {
        this.commentsError.set('Unable to delete comment.');
      }
    });
  }

  toggleReport() {
    this.reportOpen.set(!this.reportOpen());
    this.reportError.set('');
    this.reportSuccess.set('');
  }

  submitReport(event: Event, postId: number) {
    event.preventDefault();
    this.reportError.set('');
    this.reportSuccess.set('');
    this.reportLoading.set(true);
    this.reportService.createReport({
      postId,
      reason: this.reportReason(),
      description: this.reportDescription().trim() || undefined
    }).subscribe({
      next: () => {
        this.reportLoading.set(false);
        this.reportSuccess.set('Report submitted.');
        this.reportDescription.set('');
      },
      error: () => {
        this.reportLoading.set(false);
        this.reportError.set('Unable to submit report.');
      }
    });
  }
}
