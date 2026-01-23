import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="container py-5">
      <div class="text-center">
        <h1 class="display-4 fw-bold">404</h1>
        <p class="lead text-muted">The page you are looking for does not exist.</p>
        <a class="btn btn-primary" routerLink="/posts">Go to Posts</a>
      </div>
    </div>
  `,
})
export class NotFoundComponent {}
