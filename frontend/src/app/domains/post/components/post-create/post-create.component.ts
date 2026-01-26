import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PostService } from '../../services/post.service';
import { PostVisibility } from '../../models/post.model';

@Component({
  selector: 'app-post-create',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container py-4">
      <div class="row justify-content-center">
        <div class="col-lg-8">
          <div class="card shadow-sm">
            <div class="card-header bg-dark text-white">Create Post</div>
            <div class="card-body">
              <form (submit)="submit($event)">
                <div class="mb-3">
                  <label class="form-label">Title</label>
                  <input class="form-control" name="title" [ngModel]="title()" (ngModelChange)="title.set($event)" required />
                </div>

                <div class="mb-3">
                  <label class="form-label">Content</label>
                  <textarea class="form-control" name="content" rows="6" [ngModel]="content()" (ngModelChange)="content.set($event)" required></textarea>
                </div>

                <div class="mb-3">
                  <label class="form-label">Subjects (comma separated)</label>
                  <input class="form-control" name="subjects" [ngModel]="subjects()" (ngModelChange)="subjects.set($event)" placeholder="tech, angular, spring" required />
                </div>

                <div class="mb-3">
                  <label class="form-label">Visibility</label>
                  <select class="form-select" name="visibility" [ngModel]="visibility()" (ngModelChange)="visibility.set($event)" required>
                    <option [ngValue]="PostVisibility.PUBLIC">Public</option>
                    <option [ngValue]="PostVisibility.PRIVATE">Private</option>
                    <option [ngValue]="PostVisibility.CLOSEFRIEND">Close Friends</option>
                  </select>
                </div>

                <div class="mb-3">
                  <label class="form-label">Media (optional)</label>
                  <input type="file" class="form-control" multiple accept="image/*,video/*" (change)="onFilesChange($event)" />
                </div>

                <div *ngIf="error()" class="alert alert-danger">{{ error() }}</div>

                <button class="btn btn-primary" [disabled]="loading()">
                  {{ loading() ? 'Creating...' : 'Create Post' }}
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class PostCreateComponent {
  PostVisibility = PostVisibility;
  title = signal('');
  content = signal('');
  subjects = signal('');
  visibility = signal<PostVisibility>(PostVisibility.PUBLIC);
  mediaFiles = signal<File[]>([]);
  loading = signal(false);
  error = signal('');

  constructor(private postService: PostService, private router: Router) {}

  onFilesChange(event: Event) {
    const input = event.target as HTMLInputElement;
    const files = input.files ? Array.from(input.files) : [];
    this.mediaFiles.set(files);
  }

  submit(event: Event) {
    event.preventDefault();
    this.error.set('');
    const subjects = this.subjects()
      .split(',')
      .map(s => s.trim())
      .filter(Boolean);
    if (!subjects.length) {
      this.error.set('Please add at least one subject.');
      return;
    }
    if (this.mediaFiles().length> 10) {
      this.error.set('Please add at most ten Files.');
      return;
    }
    this.loading.set(true);
    this.postService.createPost({
      postTitle: this.title().trim(),
      postContent: this.content().trim(),
      postSubject: subjects,
      postVisibility: this.visibility(),
      multipartFiles: this.mediaFiles()
    }).subscribe({
      next: () => this.router.navigate(['/posts/list']),
      error: () => {
        this.loading.set(false);
        this.error.set('Unable to create post.');
      }
    });
  }
}
