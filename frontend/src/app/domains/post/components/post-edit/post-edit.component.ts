import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PostService } from '../../services/post.service';
import { PostVisibility } from '../../models/post.model';
import { catchError, map, of, switchMap } from 'rxjs';
import { ErrorService } from '@core/services/error.service';
import { DebounceClickDirective } from '@shared/directives/debounce-click.directive';

@Component({
  selector: 'app-post-edit',
  standalone: true,
  imports: [CommonModule, FormsModule, DebounceClickDirective],
  template: `
    <div class="container py-4">
      <ng-container *ngIf="loaded()">
        <div class="row justify-content-center">
          <div class="col-lg-8">
            <div class="card shadow-sm">
              <div class="card-header bg-dark text-white">Edit Post</div>
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
                    <input class="form-control" name="subjects" [ngModel]="subjects()" (ngModelChange)="subjects.set($event)" required />
                  </div>

                  <div class="mb-3">
                    <label class="form-label">Visibility</label>
                    <select class="form-select" name="visibility" [ngModel]="visibility()" (ngModelChange)="visibility.set($event)" required>
                      <option [ngValue]="PostVisibility.PUBLIC">Public</option>
                      <option [ngValue]="PostVisibility.PRIVATE">Private</option>
                      <option [ngValue]="PostVisibility.CLOSEFRIEND">Close Friends</option>
                    </select>
                  </div>

                  <div *ngIf="error()" class="alert alert-danger">{{ error() }}</div>

                  <button class="btn btn-primary" [disabled]="loading()" appDebounceClick>
                    {{ loading() ? 'Saving...' : 'Save Changes' }}
                  </button>
                </form>
              </div>
            </div>
          </div>
        </div>
      </ng-container>

      <div *ngIf="!loaded()" class="text-muted">Loading post...</div>
    </div>
  `,
})
export class PostEditComponent {
  PostVisibility = PostVisibility;
  title = signal('');
  content = signal('');
  subjects = signal('');
  visibility = signal<PostVisibility>(PostVisibility.PUBLIC);
  loading = signal(false);
  error = signal('');
  loaded = signal(false);
  postId = signal<number | null>(null);

  constructor(
    private route: ActivatedRoute,
    private postService: PostService,
    private router: Router,
    private errorService: ErrorService
  ) {
    this.route.paramMap.pipe(
      map(params => Number(params.get('id'))),
      switchMap(id => {
        if (!id || Number.isNaN(id)) {
          this.error.set('Invalid post id.');
          return of(null);
        }
        this.postId.set(id);
        return this.postService.getPostById(id).pipe(
          catchError(() => {
            this.error.set('Unable to load post.');
            return of(null);
          })
        );
      })
    ).subscribe((post) => {
      if (post) {
        this.title.set(post.postTitle || '');
        this.content.set(post.postContent || '');
        this.subjects.set((post.postSubject || []).join(', '));
        this.visibility.set(post.postVisibility || PostVisibility.PUBLIC);
      }
      this.loaded.set(true);
    });
  }

  submit(event: Event) {
    event.preventDefault();
    this.error.set('');
    const id = this.postId();
    if (!id) return;
    const subjects = this.subjects()
      .split(',')
      .map(s => s.trim())
      .filter(Boolean);
    if (!subjects.length) {
      this.error.set('Please add at least one subject.');
      return;
    }

    this.loading.set(true);
    this.postService.updatePost(id, {
      postTitle: this.title().trim(),
      postContent: this.content().trim(),
      postSubject: subjects,
      postVisibility: this.visibility()
    }).subscribe({
      next: () => {
        // this.errorService.addSuccess('Post updated successfully.');
        this.router.navigate(['/posts', id]);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Unable to update post.');
      }
    });
  }
}
