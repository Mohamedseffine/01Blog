import { Component, OnInit } from '@angular/core';
import { AsyncPipe, NgIf } from '@angular/common';
import { RouterLink, RouterOutlet } from '@angular/router';
import { catchError, map, of, switchMap } from 'rxjs';
import { AuthService } from '@core/services/auth.service';
import { Router } from '@angular/router';
import { UserService } from '@domains/user/services/user.service';
import { NotificationService } from '@domains/notification/services/notification.service';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, NgIf, AsyncPipe],
  template: `
    <div class="d-flex flex-column min-vh-100">
      <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm">
        <div class="container-fluid">
          <a class="navbar-brand fw-bold" routerLink="/">
            <i class="bi bi-pencil-square"></i> Moblogging
          </a>
          <ng-container *ngIf="isAuthenticated$ | async">
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
              <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
              <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                  <a class="nav-link" routerLink="/posts">Posts</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" routerLink="/users/list">Users</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" routerLink="/notifications">Notifications</a>
                </li>
                <li class="nav-item" *ngIf="isAdmin$ | async">
                  <a class="nav-link" routerLink="/admin/dashboard">Admin</a>
                </li>
                <li class="nav-item" *ngIf="currentUser$ | async as currentUser">
                  <a class="nav-link d-flex align-items-center gap-2" [routerLink]="['/users/profile', currentUser.id]">
                    <img
                      [src]="profileImage$ | async"
                      class="rounded-circle"
                      width="28"
                      height="28"
                      alt="Profile"
                    />
                    <span>Profile</span>
                  </a>
                </li>
                <li class="nav-item">
                  <button class="btn btn-outline-light btn-sm ms-2" type="button" (click)="logout()">
                    Logout
                  </button>
                </li>
              </ul>
            </div>
          </ng-container>
        </div>
      </nav>

      <main class="flex-grow-1 py-4">
        <div class="container-lg">
          <router-outlet></router-outlet>
        </div>
      </main>

      <footer class="bg-dark text-light py-4 mt-5 border-top">
        <div class="container-fluid text-center">
          <p class="mb-0">&copy; 2026 Moblogging. All rights reserved.</p>
        </div>
      </footer>
    </div>
  `,
})
export class MainLayoutComponent implements OnInit {
  isAdmin$ = this.authService.currentUser$.pipe(
    map(user => user?.roles?.some(role => role === 'ADMIN' || role === 'ROLE_ADMIN') ?? false)
  );
  currentUser$ = this.authService.currentUser$;
  profileImage$ = this.currentUser$.pipe(
    switchMap((user) => {
      if (!user?.id || !user.profilePicture) return of(this.fallbackAvatar);
      return this.userService.getProfilePicture(user.id).pipe(
        catchError(() => of(this.fallbackAvatar))
      );
    })
  );
  isAuthenticated$ = this.authService.currentUser$.pipe(
    map(user => !!user || !!this.authService.getToken())
  );

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    if (this.authService.getToken() && !this.authService.getCurrentUserSnapshot()) {
      this.authService.refreshCurrentUser().subscribe({
        error: () => {
          // keep silent; guard will handle unauthenticated state
        }
      });
    }

    this.authService.currentUser$.subscribe((user) => {
      this.notificationService.startRealtime(user);
    });
  }

  private fallbackAvatar =
    "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='28' height='28'><rect width='100%' height='100%' fill='%23343a40'/><text x='50%' y='60%' font-size='16' text-anchor='middle' fill='%23adb5bd' font-family='Arial'>?</text></svg>";

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.notificationService.stopRealtime();
        this.router.navigate(['/auth/login']);
      },
      error: () => {
        // still clear local state even if backend fails
        this.authService.clearToken();
        this.notificationService.stopRealtime();
        this.router.navigate(['/auth/login']);
      }
    });
  }
}
