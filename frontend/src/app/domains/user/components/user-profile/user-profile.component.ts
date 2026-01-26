import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { catchError, map, of, switchMap } from 'rxjs';
import { UserService } from '../../services/user.service';
import { AuthService } from '@core/services/auth.service';
import { ReportService } from '@domains/report/services/report.service';
import { ReportReason } from '@domains/report/models/report.model';
import { ErrorService } from '@core/services/error.service';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
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
                <span>{{ user.followersCount }} followers</span>
                <span>{{ user.followingCount }} following</span>
              </div>
            </div>
            <div class="col-md-8">
              <h4 class="mb-2">Bio</h4>
              <p class="text-muted" *ngIf="user.bio; else noBio">{{ user.bio }}</p>
              <ng-template #noBio>
                <p class="text-muted">No bio yet.</p>
              </ng-template>

              <div class="mt-4">
                <h4 class="mb-2">Posts</h4>
                <p class="text-muted">
                  {{ user.posts?.length ?? 0 }} posts
                </p>
              </div>

              <div class="mt-4">
                <a
                  class="btn btn-outline-primary"
                  routerLink="/users/edit-profile"
                  *ngIf="canEditProfile(user.id)"
                >
                  Edit profile
                </a>
                <button
                  class="btn btn-primary ms-2"
                  (click)="toggleFollow(user.id)"
                  *ngIf="canFollowUser(user.id)"
                  [disabled]="followLoading()"
                  type="button"
                >
                  {{ followLoading() ? 'Loading...' : (isFollowing() ? 'Unfollow' : 'Follow') }}
                </button>
                <button
                  class="btn btn-outline-danger ms-2"
                  *ngIf="canReportUser(user.id)"
                  (click)="toggleReport()"
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
                    <button class="btn btn-sm btn-danger" [disabled]="reportLoading()">
                      {{ reportLoading() ? 'Submitting...' : 'Submit Report' }}
                    </button>
                  </form>
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
        }),
        map((user) => {
          if (user) {
            this.isFollowing.set(user.isFollowing ?? false);
          }
          return user;
        })
      );
    })
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
    private authService: AuthService,
    private reportService: ReportService,
    private router: Router,
    private errorService: ErrorService
  ) {}

  private fallbackAvatar =
    "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='160' height='160'><rect width='100%' height='100%' fill='%23dee2e6'/><text x='50%' y='54%' font-size='48' text-anchor='middle' fill='%236c757d' font-family='Arial'>?</text></svg>";

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
        this.errorService.addSuccess('Report submitted successfully.');
      },
      error: () => {
        this.reportLoading.set(false);
        this.reportError.set('Unable to submit report.');
      }
    });
  }
}
