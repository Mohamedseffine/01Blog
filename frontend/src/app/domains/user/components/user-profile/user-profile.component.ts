import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { catchError, map, of, switchMap } from 'rxjs';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, RouterLink],
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
                <a class="btn btn-outline-primary" routerLink="/users/edit-profile">Edit profile</a>
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

  user$ = this.route.paramMap.pipe(
    map((params) => Number(params.get('id'))),
    switchMap((id) => {
      if (!id || Number.isNaN(id)) {
        this.error.set('Invalid profile id.');
        return of(null);
      }
      return this.userService.getUserById(id).pipe(
        catchError(() => {
          this.error.set('Unable to load profile.');
          return of(null);
        })
      );
    })
  );
  profileImage$ = this.user$.pipe(
    switchMap((user) => {
      if (!user?.id) return of(this.fallbackAvatar);
      return this.userService.getProfilePicture(user.id).pipe(
        catchError(() => of(this.fallbackAvatar))
      );
    })
  );

  constructor(private route: ActivatedRoute, private userService: UserService) {}

  private fallbackAvatar =
    "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='160' height='160'><rect width='100%' height='100%' fill='%23dee2e6'/><text x='50%' y='54%' font-size='48' text-anchor='middle' fill='%236c757d' font-family='Arial'>?</text></svg>";
}
