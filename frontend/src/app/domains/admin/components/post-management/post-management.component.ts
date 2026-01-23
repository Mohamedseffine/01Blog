import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { AdminService } from '../../services/admin.service';
import { AdminPost, Page } from '../../models/admin.model';

@Component({
  selector: 'app-post-management',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="container py-4">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h1 class="mb-0">Posts</h1>
        <button class="btn btn-outline-secondary btn-sm" (click)="refresh()" [disabled]="loading()">
          <i class="bi bi-arrow-clockwise"></i> Refresh
        </button>
      </div>

      <div class="card shadow-sm mb-3">
        <div class="card-body">
          <div class="row g-3">
            <div class="col-12 col-md-3">
              <label class="form-label">Visibility</label>
              <select class="form-select" [(ngModel)]="visibility">
                <option value="">All</option>
                <option value="PUBLIC">PUBLIC</option>
                <option value="PRIVATE">PRIVATE</option>
                <option value="CLOSEFRIEND">CLOSEFRIEND</option>
              </select>
            </div>
            <div class="col-12 col-md-3">
              <label class="form-label">Hidden</label>
              <select class="form-select" [(ngModel)]="hidden">
                <option value="">All</option>
                <option value="true">Hidden</option>
                <option value="false">Visible</option>
              </select>
            </div>
            <div class="col-12 col-md-3">
              <label class="form-label">Creator ID</label>
              <input class="form-control" type="number" min="1" [(ngModel)]="creatorId" placeholder="e.g. 42">
            </div>
            <div class="col-12 col-md-3">
              <label class="form-label">Creator Username</label>
              <input class="form-control" [(ngModel)]="creatorUsername" placeholder="e.g. alice">
            </div>
            <div class="col-12 col-md-3">
              <label class="form-label">Sort</label>
              <select class="form-select" [(ngModel)]="sortDir">
                <option value="desc">Newest first</option>
                <option value="asc">Oldest first</option>
              </select>
            </div>
            <div class="col-12 col-md-3 d-flex align-items-end">
              <button class="btn btn-primary w-100" (click)="applyFilters()" [disabled]="loading()">
                Apply Filters
              </button>
            </div>
          </div>
        </div>
      </div>

      <div *ngIf="loading()" class="text-muted">Loading posts...</div>
      <div *ngIf="error()" class="alert alert-danger">{{ error() }}</div>

      <div class="table-responsive" *ngIf="posts().length">
        <table class="table table-hover align-middle">
          <thead>
            <tr>
              <th>ID</th>
              <th>Title</th>
              <th>Visibility</th>
              <th>Creator</th>
              <th>Created</th>
              <th class="text-end">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let post of posts()">
              <td>{{ post.id }}</td>
              <td class="fw-semibold">{{ post.title }}</td>
              <td>
                <span class="badge bg-secondary">{{ post.visibility }}</span>
              </td>
              <td>{{ post.creatorUsername || post.creatorId || 'Unknown' }}</td>
              <td>{{ post.createdAt | date:'short' }}</td>
              <td class="text-end">
                <a class="btn btn-sm btn-outline-primary me-2" [routerLink]="['/posts', post.id]">
                  View
                </a>
                <button
                  class="btn btn-sm btn-outline-warning me-2"
                  (click)="toggleHidden(post)"
                >
                  {{ post.hidden ? 'Unhide' : 'Hide' }}
                </button>
                <button class="btn btn-sm btn-outline-danger" (click)="deletePost(post)">
                  Delete
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div *ngIf="!posts().length && !loading()" class="text-muted">No posts found.</div>

      <nav class="mt-4" *ngIf="pageInfo() as page">
        <ul class="pagination justify-content-center">
          <li class="page-item" [class.disabled]="page.first">
            <button class="page-link" (click)="changePage(page.number - 1)" [disabled]="page.first">Previous</button>
          </li>
          <li class="page-item disabled">
            <span class="page-link">Page {{ page.number + 1 }} of {{ page.totalPages }}</span>
          </li>
          <li class="page-item" [class.disabled]="page.last">
            <button class="page-link" (click)="changePage(page.number + 1)" [disabled]="page.last">Next</button>
          </li>
        </ul>
      </nav>
    </div>
  `,
})
export class PostManagementComponent implements OnInit {
  posts = signal<AdminPost[]>([]);
  pageInfo = signal<Page<AdminPost> | null>(null);
  loading = signal(false);
  error = signal('');
  visibility = '';
  hidden = '';
  creatorId = '';
  creatorUsername = '';
  sortDir = 'desc';

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.refresh();
  }

  applyFilters() {
    this.refresh(0);
  }

  refresh(page: number = 0) {
    this.loading.set(true);
    this.error.set('');
    const hiddenValue = this.hidden === '' ? undefined : this.hidden === 'true';
    const creatorIdValue = this.creatorId ? Number(this.creatorId) : undefined;
    const visibilityValue = this.visibility || undefined;
    const creatorUsernameValue = this.creatorUsername.trim() || undefined;
    this.adminService.getPosts(page, 10, {
      sortDir: this.sortDir,
      visibility: visibilityValue,
      hidden: hiddenValue,
      creatorId: creatorIdValue,
      creatorUsername: creatorUsernameValue
    }).subscribe({
      next: (res) => {
        const pageData = res.data;
        this.posts.set(pageData.content || []);
        this.pageInfo.set(pageData);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Failed to load posts.');
        this.loading.set(false);
      }
    });
  }

  changePage(page: number) {
    if (page < 0) return;
    this.refresh(page);
  }

  deletePost(post: AdminPost) {
    this.adminService.deletePost(post.id).subscribe({
      next: () => {
        this.posts.set(this.posts().filter(item => item.id !== post.id));
      },
      error: () => {
        this.error.set(`Failed to delete post #${post.id}.`);
      }
    });
  }

  toggleHidden(post: AdminPost) {
    const newHidden = !post.hidden;

    const request = newHidden
      ? this.adminService.hidePost(post.id)
      : this.adminService.unhidePost(post.id);

    request.subscribe({
      next: () => {
        this.posts.update(list =>
          list.map(p => p.id === post.id ? { ...p, hidden: newHidden } : p)
        );
      },
      error: () => {
        this.error.set(`Failed to ${post.hidden ? 'unhide' : 'hide'} post #${post.id}.`);
      }
    });
  }

}
