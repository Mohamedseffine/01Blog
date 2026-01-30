import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PostService } from '../../services/post.service';
import { Post } from '../../models/post.model';

@Component({
  selector: 'app-post-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="post-list">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="mb-0">Posts</h1>
        <a routerLink="/posts/create" class="btn btn-primary">
          <i class="bi bi-plus-circle"></i> Create Post
        </a>
      </div>

      <div *ngIf="loading()" class="text-muted">Loading posts...</div>
      <div *ngIf="error()" class="alert alert-danger">{{ error() }}</div>
      <div *ngIf="!loading() && !posts().length" class="text-muted">
        No posts yet.
      </div>

      <div class="row g-4">
        <div class="col-sm-6 col-lg-4" *ngFor="let post of posts()">
          <div class="card h-100 shadow-sm bounded-card">
            <div class="card-body">
              <h5 class="card-title text-wrap-anywhere">{{ (post.postTitle || '') | slice:0:30 }}{{ (post.postTitle?.length || 0) > 30 ? '...' : '' }}</h5>
              <p class="card-text text-muted text-truncate-multiline text-wrap-anywhere">
                {{ (post.postContent || '') | slice:0:30 }}{{ (post.postContent?.length || 0) > 30 ? '...' : '' }}
              </p>
              <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                <small class="text-muted">by {{ post.creatorUsername }}</small>
                <a [routerLink]="['/posts', post.id]" class="btn btn-sm btn-outline-primary">Read More</a>
              </div>
            </div>
            <div class="card-footer bg-light small text-muted">
              <div class="d-flex flex-wrap gap-2">
                <span class="badge bg-secondary subject-badge" *ngFor="let subject of post.postSubject">
                 {{ (subject|| '') | slice:0:30 }}{{ (subject.length || 0) > 30 ? '...' : '' }} 
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <nav aria-label="Page navigation" class="mt-5" *ngIf="totalPages() > 1">
        <ul class="pagination justify-content-center">
          <li class="page-item" [class.disabled]="page() === 0">
            <button class="page-link" type="button" (click)="loadPage(page() - 1)" [disabled]="page() === 0">Previous</button>
          </li>
          <li class="page-item active">
            <span class="page-link">{{ page() + 1 }}</span>
          </li>
          <li class="page-item" [class.disabled]="page() + 1 >= totalPages()">
            <button class="page-link" type="button" (click)="loadPage(page() + 1)" [disabled]="page() + 1 >= totalPages()">Next</button>
          </li>
        </ul>
      </nav>
    </div>
  `,
})
export class PostListComponent implements OnInit {
  posts = signal<Post[]>([]);
  loading = signal(false);
  error = signal('');
  page = signal(0);
  totalPages = signal(0);

  constructor(private postService: PostService) {}

  ngOnInit() {
    this.loadPage(0);
  }

  loadPage(page: number) {
    if (page < 0) return;
    this.loading.set(true);
    this.error.set('');
    this.postService.getPosts(page, 9).subscribe({
      next: (res) => {
        this.posts.set(res?.content ?? []);
        this.page.set(res?.number ?? 0);
        this.totalPages.set(res?.totalPages ?? 0);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Unable to load posts.');
      }
    });
  }
}
