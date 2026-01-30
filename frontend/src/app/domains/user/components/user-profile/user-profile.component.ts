import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { catchError, map, of, shareReplay, switchMap, tap } from 'rxjs';
import { UserService } from '../../services/user.service';
import { AuthService } from '@core/services/auth.service';
import { ReportService } from '@domains/report/services/report.service';
import { ReportReason } from '@domains/report/models/report.model';
import { ErrorService } from '@core/services/error.service';
import { PostService } from '@domains/post/services/post.service';
import { Post } from '@domains/post/models/post.model';
import { DebounceClickDirective } from '@shared/directives/debounce-click.directive';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, DebounceClickDirective],
  template: `
    <div class="container py-4">
      <ng-container *ngIf="user$ | async as user; else loading">
        <ng-container *ngIf="user; else errorTpl">
          <div class="row">
            <div class="col-md-4 text-center">
              <img
                [src]="profileImage$ | async"
                class="rounded-circle shadow-sm mb-3"
                alt="Profile picture"
                width="160"
                height="160"
              />
              <h3 class="mb-1">{{ user.username }}</h3>
              <p class="text-muted mb-3">{{ user.email }}</p>
              <div class="d-flex justify-content-center gap-3 small text-muted">
                <span>{{ user.followersCount }} following</span>
                <span>{{ user.followingCount }} followers</span>
              </div>
            </div>
            <div class="col-md-8">
              <h4 class="mb-2">Bio</h4>
              <p class="text-muted" *ngIf="user.bio; else noBio">{{ user.bio }}</p>
              <ng-template #noBio>
                <p class="text-muted">No bio yet.</p>
              </ng-template>

              <div class="mt-4 d-flex flex-wrap gap-2">
                <a
                  class="btn btn-outline-primary"
                  routerLink="/users/edit-profile"
                  *ngIf="canEditProfile(user.id)"
                >
                  Edit profile
                </a>
                <button
                  class="btn btn-primary"
                  appDebounceClick
                  (appDebounceClick)="toggleFollow(user.id)"
                  *ngIf="canFollowUser(user.id)"
                  [disabled]="followLoading()"
                  type="button"
                >
                  {{ followLoading() ? 'Loading...' : (isFollowing() ? 'Unfollow' : 'Follow') }}
                </button>
                <button
                  class="btn btn-outline-danger"
                  *ngIf="canReportUser(user.id)"
                  appDebounceClick
                  (appDebounceClick)="toggleReport()"
                  type="button"
                >
                  {{ reportOpen() ? 'Cancel Report' : 'Report User' }}
                </button>
              </div>

              <div class="card mt-3" *ngIf="reportOpen()">
                <div class="card-body">
                  <form (submit)="submitReport($event, user.id)">
                    <div class="mb-2">
                      <label class="form-label">Reason</label>
                      <select class="form-select" [ngModel]="reportReason()" (ngModelChange)="setReportReason($event)" name="userReason" required>
                        <option *ngFor="let reason of reportReasons" [ngValue]="reason">
                          {{ reason }}
                        </option>
                      </select>
                    </div>
                    <div class="mb-2">
                      <label class="form-label">Details (optional)</label>
                      <textarea class="form-control" rows="2" [ngModel]="reportDescription()" (ngModelChange)="setReportDescription($event)" name="userDescription"></textarea>
                    </div>
                    <div *ngIf="reportError()" class="alert alert-danger">{{ reportError() }}</div>
                    <div *ngIf="reportSuccess()" class="alert alert-success">{{ reportSuccess() }}</div>
                    <button class="btn btn-sm btn-danger" [disabled]="reportLoading()" appDebounceClick>
                      {{ reportLoading() ? 'Submitting...' : 'Submit Report' }}
                    </button>
                  </form>
                </div>
              </div>

              <div class="mt-4">
                <div class="d-flex justify-content-between align-items-center mb-2 posts-header">
                  <div>
                    <h4 class="mb-0">Posts</h4>
                    <p class="text-muted mb-0">
                      {{ postsTotal() }} posts
                    </p>
                  </div>
                  <a
                    *ngIf="canEditProfile(user.id)"
                    class="btn btn-sm btn-outline-secondary"
                    routerLink="/posts/create"
                    type="button"
                  >
                    New Post
                  </a>
                </div>

                <div class="card bounded-card">
                  <div class="card-body">
                    <div *ngIf="postsLoading()" class="text-muted">Loading posts...</div>
                    <div *ngIf="postsError()" class="alert alert-danger mb-0">{{ postsError() }}</div>
                    <div *ngIf="!postsLoading() && !posts().length && !postsError()" class="text-muted">
                      This user hasn't posted yet.
                    </div>

                    <div class="row g-3" *ngIf="posts().length && !postsError()">
                      <div class="col-md-6 posts-grid" *ngFor="let post of posts()">
                        <div class="post-card h-100 p-3 shadow-sm border">
                          <div class="d-flex justify-content-between align-items-start mb-1">
                            <h6 class="mb-1 text-wrap-anywhere">{{ (post.postTitle || '') | slice:0:30 }}{{ (post.postTitle?.length || 0) > 30 ? '...' : '' }}</h6>
                            <a
                              [routerLink]="['/posts', post.id]"
                              class="btn btn-sm btn-outline-primary"
                              type="button"
                        >
                          Open
                        </a>
                      </div>
                      <p class="text-muted small mb-2 post-snippet">
                        {{ (post.postContent || '') | slice:0:30 }}{{ (post.postContent?.length || 0) > 30 ? '...' : '' }}
                      </p>
                          <div class="d-flex flex-wrap gap-2" *ngIf="post.postSubject?.length">
                            <span class="badge bg-light text-dark border subject-badge" *ngFor="let subject of post.postSubject">
                              {{ (subject|| '') | slice:0:30 }}{{ (subject.length || 0) > 30 ? '...' : '' }} 
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>

                    <nav aria-label="User posts pagination" class="mt-3" *ngIf="postsTotalPages() > 1">
                      <ul class="pagination justify-content-center mb-0">
                        <li class="page-item" [class.disabled]="postsPage() === 0">
                          <button
                            class="page-link"
                            type="button"
                            [disabled]="postsPage() === 0 || postsLoading()"
                            (click)="loadUserPosts(user.id, postsPage() - 1)"
                          >
                            Previous
                          </button>
                        </li>
                        <li class="page-item active">
                          <span class="page-link">{{ postsPage() + 1 }}</span>
                        </li>
                        <li class="page-item" [class.disabled]="postsPage() + 1 >= postsTotalPages()">
                          <button
                            class="page-link"
                            type="button"
                            [disabled]="postsPage() + 1 >= postsTotalPages() || postsLoading()"
                            (click)="loadUserPosts(user.id, postsPage() + 1)"
                          >
                            Next
                          </button>
                        </li>
                      </ul>
                    </nav>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </ng-container>
      </ng-container>

      <ng-template #loading>
        <p class="text-muted">Loading profile...</p>
      </ng-template>

      <ng-template #errorTpl>
        <p class="text-danger">{{ error() || 'Unable to load profile.' }}</p>
      </ng-template>
    </div>
  `,
})
export class UserProfileComponent {
  error = signal<string | null>(null);
  reportOpen = signal(false);
  reportReason = signal<ReportReason>('SPAM');
  reportDescription = signal('');
  reportError = signal('');
  reportSuccess = signal('');
  reportLoading = signal(false);
  isFollowing = signal(false);
  followLoading = signal(false);
  posts = signal<Post[]>([]);
  postsLoading = signal(false);
  postsError = signal('');
  postsPage = signal(0);
  postsTotalPages = signal(0);
  postsTotal = signal(0);
  private currentProfileId: number | null = null;
  private readonly postsPageSize = 6;
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

  user$ = this.route.paramMap.pipe(
    map((params) => Number(params.get('id'))),
    switchMap((id) => {
      if (!id || Number.isNaN(id) || id < 0) {
        this.router.navigateByUrl('/not-found');
        return of(null);
      }
      return this.userService.getUserById(id).pipe(
        catchError((err) => {
          if (err?.status === 404) {
            this.router.navigateByUrl('/not-found');
          } else {
            this.error.set('Unable to load profile.');
          }
          return of(null);
        })
      );
    }),
    tap((user) => {
      if (user) {
        this.currentProfileId = user.id;
        this.isFollowing.set(user.isFollowing ?? false);
        this.loadUserPosts(user.id, 0);
      } else {
        this.currentProfileId = null;
        this.isFollowing.set(false);
        this.resetPosts();
      }
    }),
    shareReplay(1)
  );
  profileImage$ = this.user$.pipe(
    switchMap((user) => {
      if (!user?.id || !user.profilePicture) return of(this.fallbackAvatar);
      return this.userService.getProfilePicture(user.id).pipe(
        catchError(() => of(this.fallbackAvatar))
      );
    })
  );

  constructor(
    private route: ActivatedRoute,
    private userService: UserService,
    private postService: PostService,
    private authService: AuthService,
    private reportService: ReportService,
    private router: Router,
    private errorService: ErrorService
  ) {}

  private fallbackAvatar =
    "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='160' height='160'><rect width='100%' height='100%' fill='%23dee2e6'/><text x='50%' y='54%' font-size='48' text-anchor='middle' fill='%236c757d' font-family='Arial'>?</text></svg>";

  loadUserPosts(userId: number, page: number = 0) {
    if (page < 0) return;
    this.postsLoading.set(true);
    this.postsError.set('');
    this.postService.getUserPosts(userId, page, this.postsPageSize).subscribe({
      next: (res) => {
        if (this.currentProfileId !== userId) return;
        const content = res?.content ?? [];
        const currentPage = res?.number ?? page;
        const totalPages = res?.totalPages ?? 0;
        const totalElements = res?.totalElements ?? content.length;

        this.posts.set(content);
        this.postsPage.set(currentPage);
        this.postsTotalPages.set(totalPages);
        this.postsTotal.set(totalElements);
        this.postsLoading.set(false);
      },
      error: () => {
        if (this.currentProfileId !== userId) return;
        this.resetPosts();
        this.postsError.set('Unable to load posts.');
      }
    });
  }

  private resetPosts() {
    this.posts.set([]);
    this.postsPage.set(0);
    this.postsTotalPages.set(0);
    this.postsTotal.set(0);
    this.postsError.set('');
    this.postsLoading.set(false);
  }

  canEditProfile(userId: number) {
    const current = this.authService.getCurrentUserSnapshot();
    if (!current) return false;
    return current.id === userId;
  }

  setReportReason(value: ReportReason) {
    this.reportReason.set(value);
  }

  setReportDescription(value: string) {
    this.reportDescription.set(value);
  }

  canReportUser(userId: number) {
    const current = this.authService.getCurrentUserSnapshot();
    if (!current) return false;
    const isAdmin = current.roles?.includes('ADMIN') || current.roles?.includes('ROLE_ADMIN');
    return !isAdmin && current.id !== userId;
  }

  canFollowUser(userId: number) {
    const current = this.authService.getCurrentUserSnapshot();
    if (!current) return false;
    return current.id !== userId;
  }

  toggleFollow(userId: number) {
    if (this.isFollowing()) {
      this.unfollowUser(userId);
    } else {
      this.followUser(userId);
    }
  }

  followUser(userId: number) {
    this.followLoading.set(true);
    this.userService.followUser(userId).subscribe({
      next: () => {
        this.followLoading.set(false);
        this.isFollowing.set(true);
      },
      error: (err) => {
        this.followLoading.set(false);
        this.errorService.addWarning('Failed to follow user', 5000);
      }
    });
  }

  unfollowUser(userId: number) {
    this.followLoading.set(true);
    this.userService.unfollowUser(userId).subscribe({
      next: () => {
        this.followLoading.set(false);
        this.isFollowing.set(false);
      },
      error: (err) => {
        this.followLoading.set(false);
        this.errorService.addWarning('Failed to unfollow user', 5000);
      }
    });
  }

  toggleReport() {
    this.reportError.set('');
    this.reportSuccess.set('');
    this.reportOpen.set(!this.reportOpen());
  }

  submitReport(event: Event, userId: number) {
    event.preventDefault();
    this.reportError.set('');
    this.reportSuccess.set('');
    this.reportLoading.set(true);
    this.reportService.createReport({
      reportedUserId: userId,
      reason: this.reportReason(),
      description: this.reportDescription().trim() || undefined
    }).subscribe({
      next: () => {
        this.reportLoading.set(false);
        this.reportSuccess.set('Report submitted.');
        this.reportDescription.set('');
        this.reportOpen.set(false);
        // this.errorService.addSuccess('Report submitted successfully.');
      },
      error: () => {
        this.reportLoading.set(false);
        this.reportError.set('Unable to submit report.');
      }
    });
  }
}
