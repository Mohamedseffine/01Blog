import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-notification-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="notification-list">
      <h1 class="mb-4">Notifications</h1>

      <div class="btn-group mb-3" role="group">
        <input type="radio" class="btn-check" name="filter" id="all" checked>
        <label class="btn btn-outline-primary" for="all">All</label>

        <input type="radio" class="btn-check" name="filter" id="unread">
        <label class="btn btn-outline-primary" for="unread">Unread</label>

        <input type="radio" class="btn-check" name="filter" id="mentions">
        <label class="btn btn-outline-primary" for="mentions">Mentions</label>
      </div>

      <div class="list-group">
        <!-- Notification Item 1 -->
        <div class="list-group-item list-group-item-action border-left-primary">
          <div class="d-flex justify-content-between align-items-start">
            <div>
              <h6 class="mb-1 fw-bold">
                <span class="badge bg-primary">Comment</span>
                John Doe commented on your post
              </h6>
              <p class="text-muted small mb-0">"This is a great post! I love the insights..."</p>
              <small class="text-secondary">2 hours ago</small>
            </div>
            <div class="d-flex gap-2">
              <button class="btn btn-sm btn-outline-primary">Reply</button>
              <button class="btn btn-sm btn-outline-danger">
                <i class="bi bi-trash"></i>
              </button>
            </div>
          </div>
        </div>

        <!-- Notification Item 2 -->
        <div class="list-group-item list-group-item-action border-left-success">
          <div class="d-flex justify-content-between align-items-start">
            <div>
              <h6 class="mb-1 fw-bold">
                <span class="badge bg-success">Follow</span>
                Jane Smith started following you
              </h6>
              <small class="text-secondary">5 hours ago</small>
            </div>
            <div class="d-flex gap-2">
              <button class="btn btn-sm btn-primary">Follow Back</button>
              <button class="btn btn-sm btn-outline-danger">
                <i class="bi bi-trash"></i>
              </button>
            </div>
          </div>
        </div>

        <!-- Notification Item 3 -->
        <div class="list-group-item list-group-item-action border-left-info">
          <div class="d-flex justify-content-between align-items-start">
            <div>
              <h6 class="mb-1 fw-bold">
                <span class="badge bg-info">Like</span>
                5 people liked your post
              </h6>
              <small class="text-secondary">1 day ago</small>
            </div>
            <button class="btn btn-sm btn-outline-danger">
              <i class="bi bi-trash"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div class="alert alert-info mt-4" role="alert">
        <i class="bi bi-info-circle"></i>
        You're all caught up! No new notifications.
      </div>
    </div>
  `,
  styles: [`
    .border-left-primary {
      border-left: 4px solid var(--bs-primary) !important;
    }
    .border-left-success {
      border-left: 4px solid var(--bs-success) !important;
    }
    .border-left-info {
      border-left: 4px solid var(--bs-info) !important;
    }
  `],
})
export class NotificationListComponent { }
