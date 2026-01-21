import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../services/admin.service';
import { AdminComment, Page } from '../../models/admin.model';

@Component({
  selector: 'app-comment-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container py-4">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h1 class="mb-0">Comments</h1>
        <button class="btn btn-outline-secondary btn-sm" (click)="refresh()" [disabled]="loading()">
          <i class="bi bi-arrow-clockwise"></i> Refresh
        </button>
      </div>

      <div class="card shadow-sm mb-3">
        <div class="card-body">
          <div class="row g-3">
            <div class="col-12 col-md-3">
              <label class="form-label">Hidden</label>
              <select class="form-select" [(ngModel)]="hidden">
                <option value="">All</option>
                <option value="true">Hidden</option>
                <option value="false">Visible</option>
              </select>
            </div>
            <div class="col-12 col-md-3">
              <label class="form-label">Post ID</label>
              <input class="form-control" type="number" min="1" [(ngModel)]="postId" placeholder="e.g. 120">
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

      <div *ngIf="loading()" class="text-muted">Loading comments...</div>
      <div *ngIf="error()" class="alert alert-danger">{{ error() }}</div>

      <div class="table-responsive" *ngIf="comments().length">
        <table class="table table-hover align-middle">
          <thead>
            <tr>
              <th>ID</th>
              <th>Content</th>
              <th>Post</th>
              <th>Creator</th>
              <th>Created</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let comment of comments()">
              <td>{{ comment.id }}</td>
              <td>{{ comment.content }}</td>
              <td>{{ comment.postId || 'Unknown' }}</td>
              <td>{{ comment.creatorUsername || comment.creatorId || 'Unknown' }}</td>
              <td>{{ comment.createdAt | date:'short' }}</td>
              <td>
                <span class="badge" [class.bg-secondary]="comment.hidden" [class.bg-success]="!comment.hidden">
                  {{ comment.hidden ? 'Hidden' : 'Visible' }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div *ngIf="!comments().length && !loading()" class="text-muted">No comments found.</div>

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
export class CommentManagementComponent implements OnInit {
  comments = signal<AdminComment[]>([]);
  pageInfo = signal<Page<AdminComment> | null>(null);
  loading = signal(false);
  error = signal('');
  hidden = '';
  postId = '';
  creatorId = '';
  creatorUsername = '';
  sortDir = 'desc';

  constructor(private adminService: AdminService) {}

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
    const postIdValue = this.postId ? Number(this.postId) : undefined;
    const creatorIdValue = this.creatorId ? Number(this.creatorId) : undefined;
    const creatorUsernameValue = this.creatorUsername.trim() || undefined;
    this.adminService.getComments(page, 10, {
      sortDir: this.sortDir,
      hidden: hiddenValue,
      postId: postIdValue,
      creatorId: creatorIdValue,
      creatorUsername: creatorUsernameValue
    }).subscribe({
      next: (res) => {
        const pageData = res.data;
        this.comments.set(pageData.content || []);
        this.pageInfo.set(pageData);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Failed to load comments.');
        this.loading.set(false);
      }
    });
  }

  changePage(page: number) {
    if (page < 0) return;
    this.refresh(page);
  }
}
