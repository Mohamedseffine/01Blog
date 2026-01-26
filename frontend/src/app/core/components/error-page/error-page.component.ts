import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-error-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="d-flex align-items-center justify-content-center min-vh-100 bg-light">
      <div class="card shadow-sm border-0" style="max-width: 520px; width: 100%;">
        <div class="card-body p-4 text-center">
          <div class="display-4 fw-bold text-danger">{{ status }}</div>
          <h2 class="h4 mt-2 mb-3">Something went wrong</h2>
          <p class="text-muted mb-4">{{ message }}</p>
          <div class="d-flex justify-content-center gap-2">
            <button class="btn btn-primary" type="button" (click)="goHome()">
              Back to Home
            </button>
            <button class="btn btn-outline-secondary" type="button" (click)="goBack()">
              Go Back
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ErrorPageComponent {
  status = this.route.snapshot.queryParamMap.get('status') || 'Error';
  message = this.route.snapshot.queryParamMap.get('message') || 'Please try again or contact support.';

  constructor(private route: ActivatedRoute, private router: Router) {}

  goHome() {
    this.router.navigate(['/']);
  }

  goBack() {
    if (window.history.length > 1) {
      window.history.back();
      return;
    }
    this.goHome();
  }
}
