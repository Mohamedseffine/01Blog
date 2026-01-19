import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
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
              <form>
                <div class="mb-3">
                  <label for="email" class="form-label">Email Address</label>
                  <input 
                    type="email" 
                    class="form-control form-control-lg" 
                    id="email"
                    placeholder="you@example.com"
                    [(ngModel)]="email"
                    name="email"
                  >
                </div>

                <div class="mb-3">
                  <label for="password" class="form-label">Password</label>
                  <input 
                    type="password" 
                    class="form-control form-control-lg" 
                    id="password"
                    placeholder="••••••••"
                    [(ngModel)]="password"
                    name="password"
                  >
                </div>

                <div class="mb-2 form-check">
                  <input 
                    type="checkbox" 
                    class="form-check-input" 
                    id="remember"
                    [(ngModel)]="rememberMe"
                    name="rememberMe"
                  >
                  <label class="form-check-label" for="remember">
                    Remember me
                  </label>
                </div>

                <button type="submit" class="btn btn-primary btn-lg w-100 mt-3">
                  <i class="bi bi-box-arrow-in-right"></i> Sign In
                </button>
              </form>

              <div class="text-center mt-3">
                <p class="text-muted">Don't have an account? 
                  <a href="/auth/register" class="text-primary text-decoration-none fw-bold">Sign up here</a>
                </p>
              </div>
            </div>
          </div>

          <!-- Social Login -->
          <div class="mt-4">
            <div class="text-center mb-3">
              <span class="text-muted small">Or continue with</span>
            </div>
            <div class="d-grid gap-2">
              <button type="button" class="btn btn-outline-secondary">
                <i class="bi bi-google"></i> Google
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class LoginComponent {
  email = '';
  password = '';
  rememberMe = false;
}
