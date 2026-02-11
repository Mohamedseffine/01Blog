import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../../core/services/auth.service';
import { Router } from '@angular/router';
import { WebSocketService } from '@core/services/websocket.service';
import { NotificationService } from '@domains/notification/services/notification.service';
import { RouterLink } from '@angular/router';


@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="container py-4">
      <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
          <div class="card shadow-sm">
            <div class="card-header bg-primary text-white">Create an account</div>
            <div class="card-body p-4">
              <form #registerForm="ngForm" (submit)="submit($event, registerForm)" novalidate>
                <div class="row">
                  <div class="col-md-6 mb-3">
                    <label class="form-label">First name</label>
                    <input class="form-control" name="firstName" [ngModel]="model().firstName" (ngModelChange)="updateModel({ firstName: $event })" required minlength="2" maxlength="30" #firstNameCtrl="ngModel" />
                    <div class="text-danger small mt-1" *ngIf="(registerForm.submitted || firstNameCtrl.touched) && firstNameCtrl.invalid">
                      First name is required (2-30 characters).
                    </div>
                  </div>
                  <div class="col-md-6 mb-3">
                    <label class="form-label">Last name</label>
                    <input class="form-control" name="lastName" [ngModel]="model().lastName" (ngModelChange)="updateModel({ lastName: $event })" required minlength="2" maxlength="30" #lastNameCtrl="ngModel" />
                    <div class="text-danger small mt-1" *ngIf="(registerForm.submitted || lastNameCtrl.touched) && lastNameCtrl.invalid">
                      Last name is required (2-30 characters).
                    </div>
                  </div>
                </div>

                <div class="mb-3">
                  <label class="form-label">Username</label>
                  <input class="form-control" name="username" [ngModel]="model().username" (ngModelChange)="updateModel({ username: $event })" required minlength="3" maxlength="15" #usernameCtrl="ngModel" />
                  <div class="text-danger small mt-1" *ngIf="(registerForm.submitted || usernameCtrl.touched) && usernameCtrl.invalid">
                    Username is required (3-15 characters).
                  </div>
                </div>

                <div class="mb-3">
                  <label class="form-label">Email (Gmail preferred)</label>
                  <input type="email" class="form-control" name="email" [ngModel]="model().email" (ngModelChange)="updateModel({ email: $event })" required maxlength="120" #emailCtrl="ngModel" />
                  <div class="text-danger small mt-1" *ngIf="(registerForm.submitted || emailCtrl.touched) && emailCtrl.invalid">
                    Valid email is required (max 120 characters).
                  </div>
                </div>

                <div class="row">
                  <div class="col-md-6 mb-3">
                    <label class="form-label">Date of birth</label>
                    <input type="date" class="form-control" name="birthDate" [ngModel]="model().birthDate" (ngModelChange)="updateModel({ birthDate: $event })" #birthCtrl="ngModel" />
                  </div>
                  <div class="col-md-6 mb-3">
                    <label class="form-label">Gender</label>
                    <select class="form-select" name="gender" [ngModel]="model().gender" (ngModelChange)="updateModel({ gender: $event })" required #genderCtrl="ngModel">
                      <option value="MALE">Male</option>
                      <option value="FEMALE">Female</option>
                      <option value="OTHER">Other</option>
                      <option value="PREFER_NOT_TO_SAY">Prefer not to say</option>
                    </select>
                    <div class="text-danger small mt-1" *ngIf="(registerForm.submitted || genderCtrl.touched) && genderCtrl.invalid">
                      Please select a gender.
                    </div>
                  </div>
                </div>

                <div class="mb-3">
                  <label class="form-label">Profile type</label>
                  <select class="form-select" name="profileType" [ngModel]="model().profileType" (ngModelChange)="updateModel({ profileType: $event })" required #profileTypeCtrl="ngModel">
                    <option value="PUBLIC">Public</option>
                    <option value="PRIVATE">Private</option>
                  </select>
                  <div class="text-danger small mt-1" *ngIf="(registerForm.submitted || profileTypeCtrl.touched) && profileTypeCtrl.invalid">
                    Please choose a profile type.
                  </div>
                </div>

                <div class="mb-3">
                  <label class="form-label">Profile image</label>
                  <input type="file" class="form-control" accept="image/*" (change)="onFileChange($event)" />
                </div>

                <div class="row">
                  <div class="col-md-6 mb-3">
                    <label class="form-label"></label>
                    <input type="password" class="form-control" name="password" [ngModel]="model().password" (ngModelChange)="updateModel({ password: $event })" minlength="10" maxlength="72" required #passwordCtrl="ngModel" />
                    <div class="text-danger small mt-1" *ngIf="(registerForm.submitted || passwordCtrl.touched) && passwordCtrl.invalid">
                      Password must be between 10 and 72 characters.
                    </div>
                  </div>
                  <div class="col-md-6 mb-3">
                    <label class="form-label">Confirm password</label>
                    <input type="password" class="form-control" name="confirmPassword" [ngModel]="model().confirmPassword" (ngModelChange)="updateModel({ confirmPassword: $event })" minlength="10" maxlength="72" required #confirmCtrl="ngModel" />
                    <div class="text-danger small mt-1" *ngIf="(registerForm.submitted || confirmCtrl.touched) && passwordMismatch()">
                      Passwords must match.
                    </div>
                  </div>
                </div>

                <div *ngIf="error()" class="alert alert-danger">{{ error() }}</div>

                <button class="btn btn-success w-100" [disabled]="loading() || registerForm.invalid || passwordMismatch()">
                  {{ loading() ? 'Creating...' : 'Create account' }}
                </button>
              </form>

              <div class="text-center mt-3">
                <p class="text-muted">Don't have an account? 
                  <a routerLink="/auth/login" class="text-primary text-decoration-none fw-bold">Sign in here</a>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class RegisterComponent {
  model = signal({
    firstName: '', lastName: '', username: '', email: '', password: '', confirmPassword: '', birthDate: '', gender: 'MALE', profileType: 'PUBLIC'
  });
  profilePicture = signal<File | null>(null);
  loading = signal(false);
  error = signal('');

  constructor(private auth: AuthService, private router: Router,
    private ws: WebSocketService, private notificationService: NotificationService) { }

  updateModel(patch: Partial<{
    firstName: string;
    lastName: string;
    username: string;
    email: string;
    password: string;
    confirmPassword: string;
    birthDate: string;
    gender: string;
    profileType: string;
  }>) {
    this.model.update((current) => ({ ...current, ...patch }));
  }

  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files && input.files[0] ? input.files[0] : null;
    this.profilePicture.set(file);
  }

  passwordMismatch(): boolean {
    const model = this.model();
    return !!model.password && !!model.confirmPassword && model.password !== model.confirmPassword;
  }

  submit(e: Event, form: any) {
    e.preventDefault();
    this.error.set('');
    if (form?.invalid || this.passwordMismatch()) {
      form?.control?.markAllAsTouched?.();
      if (this.passwordMismatch()) {
        this.error.set('Passwords do not match');
      }
      return;
    }
    const model = this.model();
    const trimmed = {
      firstName: model.firstName.trim(),
      lastName: model.lastName.trim(),
      username: model.username.trim(),
      email: model.email.trim(),
      password: model.password,
      confirmPassword: model.confirmPassword,
      birthDate: model.birthDate,
      gender: model.gender,
      profileType: model.profileType
    };
    this.loading.set(true);
    const payload = { ...trimmed, profilePicture: this.profilePicture() || undefined };
    // ensure birthDate is ISO string if set
    if (payload.birthDate) payload.birthDate = payload.birthDate;

    this.auth.register(payload).subscribe({
      next: (res) => {
        const access = res?.data?.accessToken || res?.data?.token || res?.data;
        if (access) {
          this.auth.setToken(access);
          this.ws.connect(access, (data) => this.notificationService.onNotificationReceived(data));
        }
        this.router.navigateByUrl('/');
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err?.error?.message || 'Registration failed');
      }
    });
  }
}
