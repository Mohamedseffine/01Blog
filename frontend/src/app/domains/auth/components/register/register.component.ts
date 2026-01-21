import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../../core/services/auth.service';
import { Router } from '@angular/router';
import { WebSocketService } from '@core/services/websocket.service';
import { NotificationService } from '@domains/notification/services/notification.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container py-4">
      <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
          <div class="card shadow-sm">
            <div class="card-header bg-primary text-white">Create an account</div>
            <div class="card-body p-4">
              <form (submit)="submit($event)">
                <div class="row">
                  <div class="col-md-6 mb-3">
                    <label class="form-label">First name</label>
                    <input class="form-control" name="firstName" [ngModel]="model().firstName" (ngModelChange)="updateModel({ firstName: $event })" required minlength="3" maxlength="15" />
                  </div>
                  <div class="col-md-6 mb-3">
                    <label class="form-label">Last name</label>
                    <input class="form-control" name="lastName" [ngModel]="model().lastName" (ngModelChange)="updateModel({ lastName: $event })" required minlength="3" maxlength="15" />
                  </div>
                </div>

                <div class="mb-3">
                  <label class="form-label">Username</label>
                  <input class="form-control" name="username" [ngModel]="model().username" (ngModelChange)="updateModel({ username: $event })" required minlength="3" maxlength="15" />
                </div>

                <div class="mb-3">
                  <label class="form-label">Email (Gmail preferred)</label>
                  <input type="email" class="form-control" name="email" [ngModel]="model().email" (ngModelChange)="updateModel({ email: $event })" required />
                </div>

                <div class="row">
                  <div class="col-md-6 mb-3">
                    <label class="form-label">Date of birth</label>
                    <input type="date" class="form-control" name="birthDate" [ngModel]="model().birthDate" (ngModelChange)="updateModel({ birthDate: $event })" />
                  </div>
                  <div class="col-md-6 mb-3">
                    <label class="form-label">Gender</label>
                    <select class="form-select" name="gender" [ngModel]="model().gender" (ngModelChange)="updateModel({ gender: $event })" required>
                      <option value="MALE">Male</option>
                      <option value="FEMALE">Female</option>
                      <option value="OTHER">Other</option>
                      <option value="PREFER_NOT_TO_SAY">Prefer not to say</option>
                    </select>
                  </div>
                </div>

                <div class="mb-3">
                  <label class="form-label">Profile type</label>
                  <select class="form-select" name="profileType" [ngModel]="model().profileType" (ngModelChange)="updateModel({ profileType: $event })" required>
                    <option value="PUBLIC">Public</option>
                    <option value="PRIVATE">Private</option>
                  </select>
                </div>

                <div class="row">
                  <div class="col-md-6 mb-3">
                    <label class="form-label">Password</label>
                    <input type="password" class="form-control" name="password" [ngModel]="model().password" (ngModelChange)="updateModel({ password: $event })" minlength="10" maxlength="32" required />
                  </div>
                  <div class="col-md-6 mb-3">
                    <label class="form-label">Confirm password</label>
                    <input type="password" class="form-control" name="confirmPassword" [ngModel]="model().confirmPassword" (ngModelChange)="updateModel({ confirmPassword: $event })" minlength="10" maxlength="32" required />
                  </div>
                </div>

                <div *ngIf="error()" class="alert alert-danger">{{ error() }}</div>

                <button class="btn btn-success w-100" [disabled]="loading()">{{ loading() ? 'Creating...' : 'Create account' }}</button>
              </form>
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
  loading = signal(false);
  error = signal('');

  constructor(private auth: AuthService, private router: Router,
              private ws: WebSocketService, private notificationService: NotificationService) {}

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

  submit(e: Event) {
    e.preventDefault();
    this.error.set('');
    const model = this.model();
    if (model.password !== model.confirmPassword) {
      this.error.set('Passwords do not match');
      return;
    }
    // enforce backend password length constraint (10-32 chars)
    if (!model.password || model.password.length < 10 || model.password.length > 32) {
      this.error.set('Password must be between 10 and 32 characters');
      return;
    }
    this.loading.set(true);
    const payload = { ...model };
    // ensure birthDate is ISO string if set
    if (payload.birthDate) payload.birthDate = payload.birthDate;

    this.auth.register(payload).subscribe({
      next: (res) => {
        const access = res?.data?.accessToken || res?.data?.token || res?.data;
        const refresh = res?.data?.refreshToken;
        if (access) {
          this.auth.setToken(access);
          if (refresh) this.auth.setRefreshToken(refresh);
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
