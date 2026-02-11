import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { AuthService } from '@core/services/auth.service';
import { User } from '../../models/user.model';
import { DebounceClickDirective } from '@shared/directives/debounce-click.directive';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, DebounceClickDirective],
  template: `
    <div class="container py-4">
      <div class="row justify-content-center">
        <div class="col-md-8">
          <div class="card shadow-sm">
            <div class="card-header bg-dark text-white">
              <h4 class="mb-0">Edit Profile</h4>
            </div>
            <div class="card-body">
              <form #profileForm="ngForm" (submit)="submit($event, profileForm)" novalidate>
                <div class="mb-3">
                  <label for="username" class="form-label">Username</label>
                  <input
                    id="username"
                    type="text"
                    class="form-control"
                    name="username"
                    [ngModel]="username()"
                    (ngModelChange)="username.set($event)"
                    #usernameCtrl="ngModel"
                    required
                    minlength="3"
                    maxlength="15"
                  />
                  <div class="text-danger small mt-1" *ngIf="(profileForm.submitted || usernameCtrl.touched) && usernameCtrl.invalid">
                    Username is required (3-15 characters).
                  </div>
                </div>

                <div class="mb-3">
                  <label for="bio" class="form-label">Bio</label>
                  <textarea
                    id="bio"
                    class="form-control"
                    name="bio"
                    rows="4"
                    [ngModel]="bio()"
                    (ngModelChange)="bio.set($event)"
                    #bioCtrl="ngModel"
                    maxlength="280"
                  ></textarea>
                  <div class="text-danger small mt-1" *ngIf="(profileForm.submitted || bioCtrl.touched) && bioCtrl.invalid">
                    Bio must be 280 characters or fewer.
                  </div>
                </div>

                <div class="mb-3">
                  <label for="profilePicture" class="form-label">Profile Picture</label>
                  <input
                    id="profilePicture"
                    type="file"
                    class="form-control"
                    (change)="onFileChange($event)"
                    accept="image/*"
                  />
                </div>

                <div *ngIf="error()" class="alert alert-danger">{{ error() }}</div>
                <div *ngIf="success()" class="alert alert-success">{{ success() }}</div>

                <button type="submit" class="btn btn-primary" [disabled]="loading() || profileForm.invalid" appDebounceClick>
                  <span *ngIf="!loading()">Save changes</span>
                  <span *ngIf="loading()">Saving...</span>
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class EditProfileComponent {
  userId = signal<number | null>(null);
  username = signal('');
  bio = signal('');
  profilePicture = signal<File | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);
  success = signal<string | null>(null);

  constructor(private userService: UserService, private router: Router, private authService: AuthService) {
    this.loadCurrentUser();
  }

  loadCurrentUser() {
    this.userService.getCurrentUser().subscribe({
      next: (user: User) => {
        this.userId.set(user.id);
        this.username.set(user.username || '');
        this.bio.set(user.bio || '');
      },
      error: () => {
        this.error.set('Unable to load your profile.');
      }
    });
  }

  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files && input.files[0] ? input.files[0] : null;
    this.profilePicture.set(file);
  }

  submit(event: Event, form: any) {
    event.preventDefault();
    this.error.set(null);
    this.success.set(null);
    if (form?.invalid) {
      form?.control?.markAllAsTouched?.();
      return;
    }
    const id = this.userId();
    if (!id) {
      this.error.set('Missing user id.');
      return;
    }
    this.loading.set(true);
    this.userService.updateProfile({
      username: this.username().trim(),
      bio: this.bio().trim(),
      profilePicture: this.profilePicture() || undefined
    }).subscribe({
      next: () => {
        this.loading.set(false);
        // this.success.set('Profile updated successfully.');
        this.authService.refreshCurrentUser().subscribe({
          next: () => this.router.navigate(['/users/profile', id]),
          error: () => this.router.navigate(['/users/profile', id])
        });
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Unable to update profile.');
      }
    });
  }
}
