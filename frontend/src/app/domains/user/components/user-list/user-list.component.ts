import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormControl } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Observable, BehaviorSubject, debounceTime, distinctUntilChanged, map } from 'rxjs';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import { startWith } from 'rxjs';
import { ErrorService } from '@core/services/error.service';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="container py-4">
      <div class="row mb-4 align-items-end">
        <div class="col-md-6">
          <h2>Users</h2>
        </div>
        <div class="col-md-6 d-flex gap-2">
          <input
            type="text"
            class="form-control"
            placeholder="Search by name, username, or email..."
            [formControl]="searchControl"
          />
          <button
            class="btn btn-outline-secondary"
            (click)="refreshUsers()"
            [disabled]="loading$ | async"
            title="Refresh users list"
          >
            <i class="bi bi-arrow-clockwise"></i>
            <span *ngIf="!(loading$ | async)" class="d-none d-sm-inline ms-1">Refresh</span>
            <span *ngIf="loading$ | async" class="spinner-border spinner-border-sm ms-1" role="status" aria-hidden="true"></span>
          </button>
        </div>
      </div>

      <div *ngIf="!(loading$ | async); else loadingTpl">
        <div *ngIf="(filteredUsers$ | async) as users">
          <ng-container *ngIf="users.length > 0; else noResults">
            <div class="row">
              <div class="col-md-6 col-lg-4 mb-3" *ngFor="let user of users">
                <div class="card h-100 shadow-sm hover-shadow">
                  <div class="card-body">
                    <div class="mb-3">
                      <img
                        [src]="getProfileImage(user)"
                        class="rounded-circle mb-2"
                        alt="Profile picture"
                        width="80"
                        height="80"
                        onerror="this.src='data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%2280%22 height=%2280%22%3E%3Crect fill=%22%23ddd%22 width=%2280%22 height=%2280%22/%3E%3C/svg%3E'"
                      />
                    </div>
                    <h5 class="card-title">
                      <a [routerLink]="['/users/profile', user.id]" class="text-decoration-none">
                        {{ user.username }}
                      </a>
                    </h5>
                    <p class="card-text text-muted small">{{ user.email }}</p>
                    <div class="d-flex justify-content-between text-muted small mb-3">
                      <span>{{ user.followersCount }} followers</span>
                      <span>{{ user.followingCount }} following</span>
                    </div>
                    <p class="card-text text-muted small">
                      {{ user.bio || 'No bio' }}
                    </p>
                    <a
                      [routerLink]="['/users/profile', user.id]"
                      class="btn btn-sm btn-primary"
                    >
                      View Profile
                    </a>
                  </div>
                </div>
              </div>
            </div>

            <div class="text-muted text-center mt-3">
              <small>Showing {{ users.length }} users</small>
            </div>
          </ng-container>
        </div>
      </div>

      <ng-template #loadingTpl>
        <div class="text-center py-4">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
          <p class="text-muted mt-2">Loading users...</p>
        </div>
      </ng-template>

      <ng-template #noResults>
        <div class="alert alert-info text-center py-4">
          <p class="mb-0">No users found matching your search.</p>
        </div>
      </ng-template>
    </div>
  `,
  styles: [`
    .hover-shadow {
      transition: box-shadow 0.3s ease-in-out;
    }
    .hover-shadow:hover {
      box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
    }
  `]
})
export class UserListComponent implements OnInit {
  searchControl = new FormControl('');
  
  loading$ = new BehaviorSubject<boolean>(true);
  allUsers$ = new BehaviorSubject<User[]>([]);
  
  filteredUsers$: Observable<User[]>;

  constructor(
    private userService: UserService,
    private errorService: ErrorService
  ) {
    // Filter users based on search input
    this.filteredUsers$ = this.searchControl.valueChanges.pipe(
    startWith(this.searchControl.value ?? ''), // ✅ emit initially
      debounceTime(300),
      distinctUntilChanged(),
      map((searchTerm) => {
        const users = this.allUsers$.getValue();
        
        if (!searchTerm || !searchTerm.trim()) {
          return users;
        }

        const lowerSearch = searchTerm.toLowerCase().trim();
        return users.filter(user => {
          const username = (user.username || '').toLowerCase();
          const email = (user.email || '').toLowerCase();
          const bio = (user.bio || '').toLowerCase();
          
          return (
            username.includes(lowerSearch) ||
            email.includes(lowerSearch) ||
            bio.includes(lowerSearch)
          );
        });
      })
    );
  }

  ngOnInit(): void {
    // Load all users once on init
    this.userService.getAllUsers(0, 10000).subscribe({
      next: (response) => {
        // The response structure is: { success, message, data: { content: [], totalPages, ... } }
        const pageData = response?.data;
        let content: User[] = [];
        
        if (pageData?.content && Array.isArray(pageData.content)) {
          content = pageData.content;
        } else if (Array.isArray(pageData)) {
          content = pageData;
        }
        
        this.allUsers$.next(content);
        this.loading$.next(false);
      },
      error: (err) => {
        console.error('Failed to load users:', err);
        this.errorService.addWarning('Failed to load users', 5000);
        this.loading$.next(false);
      }
    });
  }

  getProfileImage(user: User): string {
    if (user.profilePicture) {
      return user.profilePicture;
    }
    return 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%2280%22 height=%2280%22%3E%3Crect fill=%22%23ddd%22 width=%2280%22 height=%2280%22/%3E%3C/svg%3E';
  }

  refreshUsers(): void {
    this.loading$.next(true);
    this.userService.getAllUsers(0, 10000).subscribe({
      next: (response) => {
        // The response structure is: { success, message, data: { content: [], totalPages, ... } }
        const pageData = response?.data;
        let content: User[] = [];
        
        if (pageData?.content && Array.isArray(pageData.content)) {
          content = pageData.content;
        } else if (Array.isArray(pageData)) {
          content = pageData;
        }
        
        this.allUsers$.next(content);
        this.loading$.next(false);
        this.errorService.addWarning(`Refreshed! ${content.length} users loaded`, 2000);
      },
      error: (err) => {
        console.error('Failed to refresh users:', err);
        this.errorService.addWarning('Failed to refresh users', 5000);
        this.loading$.next(false);
      }
    });
  }
}
