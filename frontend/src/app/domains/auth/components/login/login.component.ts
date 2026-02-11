import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../../core/services/auth.service';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { WebSocketService } from '@core/services/websocket.service';
import { NotificationService } from '@domains/notification/services/notification.service';
import { DebounceClickDirective } from '@shared/directives/debounce-click.directive';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, DebounceClickDirective],
  template: `
    <div class="login-container">
      <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
          <div class="card shadow-lg">
            <div class="card-header bg-primary text-white">
              <h3 class="mb-0">
                <i class="bi bi-box-arrow-in-right"></i> Login to Moblogging
              </h3>
            </div>
            <div class="card-body p-4">
              <form #loginForm="ngForm" (submit)="submit($event, loginForm)">
                <div class="mb-3">
                  <label for="email" class="form-label">Username or Email</label>
                  <input 
                    type="text" 
                    class="form-control form-control-lg" 
                    id="email"
                    placeholder="username or you@example.com"
                    [ngModel]="email()"
                    (ngModelChange)="email.set($event)"
                    name="email"
                    #emailCtrl="ngModel"
                    required
                    minlength="3"
                    maxlength="72"
                  >
                  <div class="text-danger small mt-1" *ngIf="(loginForm.submitted || emailCtrl.touched) && emailCtrl.invalid">
                    Username or email is required (3-72 characters).
                  </div>
                </div>

                <div class="mb-3">
                  <label for="password" class="form-label">Password</label>
                  <input 
                    type="password" 
                    class="form-control form-control-lg" 
                    id="password"
                    placeholder="••••••••"
                    [ngModel]="password()"
                    (ngModelChange)="password.set($event)"
                    name="password"
                    #passwordCtrl="ngModel"
                    required
                    minlength="8"
                    maxlength="72"
                  >
                  <div class="text-danger small mt-1" *ngIf="(loginForm.submitted || passwordCtrl.touched) && passwordCtrl.invalid">
                    Password must be between 8 and 72 characters.
                  </div>
                </div>

                

                <button type="submit" class="btn btn-primary btn-lg w-100 mt-3" [disabled]="loading() || loginForm.invalid" appDebounceClick>
                  <i class="bi bi-box-arrow-in-right"></i>
                  <span *ngIf="!loading()">Sign In</span>
                  <span *ngIf="loading()">Signing in...</span>
                </button>
              </form>

              <div class="text-center mt-3">
                <p class="text-muted">Don't have an account? 
                  <a routerLink="/auth/register" class="text-primary text-decoration-none fw-bold">Sign up here</a>
                </p>
              </div>
            </div>
          </div>

          
        </div>
      </div>
    </div>
  `,
})
export class LoginComponent {
  email = signal('');
  password = signal('');
  loading = signal(false);

  constructor(private auth: AuthService, private router: Router,
              private ws: WebSocketService, private notificationService: NotificationService) {}

  submit(e: Event, form: any) {
    e.preventDefault();
    const email = this.email().trim();
    const password = this.password();
    if (form?.invalid || !email || !password) {
      form?.control?.markAllAsTouched?.();
      return;
    }
    this.loading.set(true);
    this.auth.login({ usernameOrEmail: email, password })
      .subscribe({
        next: (res) => {
          // backend returns ApiResponse<AuthResponseDto>
          const access = res?.data?.accessToken || res?.data?.token || res?.data;
          if (access) {
            this.auth.setToken(access);
            // connect websocket after login
            this.ws.connect(access, (data) => this.notificationService.onNotificationReceived(data));
          }
          this.router.navigateByUrl('/');
        },
        error: () => {
          this.loading.set(false);
        }
      });
  }
}
