import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="d-flex flex-column min-vh-100">
      <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm">
        <div class="container-fluid">
          <a class="navbar-brand fw-bold" href="/">
            <i class="bi bi-pencil-square"></i> Moblogging
          </a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
              <li class="nav-item"><a class="nav-link" href="/posts">Posts</a></li>
              <li class="nav-item"><a class="nav-link" href="/notifications">Notifications</a></li>
              <li class="nav-item"><a class="nav-link" href="/users/profile/1">Profile</a></li>
              <li class="nav-item"><a class="nav-link" href="/auth/login">Login</a></li>
            </ul>
          </div>
        </div>
      </nav>

      <main class="flex-grow-1 py-4">
        <div class="container-lg">
          <router-outlet></router-outlet>
        </div>
      </main>

      <footer class="bg-dark text-light py-4 mt-5 border-top">
        <div class="container-fluid text-center">
          <p class="mb-0">&copy; 2026 Moblogging. All rights reserved.</p>
        </div>
      </footer>
    </div>
  `,
})
export class MainLayoutComponent { }
