import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Notification, NotificationType } from '../../models/notification.model';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-notification-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="notification-list">
      <h1 class="mb-4">Notifications</h1>

      <div class="btn-group mb-3" role="group">
        <button class="btn btn-outline-primary" [class.active]="filter() === 'all'" (click)="setFilter('all')">
          All
        </button>
        <button class="btn btn-outline-primary" [class.active]="filter() === 'unread'" (click)="setFilter('unread')">
          Unread
        </button>
        <button class="btn btn-outline-primary" [class.active]="filter() === 'mentions'" (click)="setFilter('mentions')">
          Mentions
        </button>
      </div>

      <div *ngIf="loading()" class="text-muted">Loading notifications...</div>
      <div *ngIf="error()" class="alert alert-danger">{{ error() }}</div>

      <div class="list-group" *ngIf="!loading() && filteredNotifications().length">
        <div
          class="list-group-item list-group-item-action"
          [class.list-group-item-light]="!n.isRead"
          [class.border-left-primary]="n.type === 'COMMENT'"
          [class.border-left-success]="n.type === 'FOLLOW'"
          [class.border-left-info]="n.type === 'REACT'"
          [class.border-left-warning]="n.type === 'POST'"
          *ngFor="let n of filteredNotifications()"
        >
          <div class="d-flex justify-content-between align-items-start">
            <div>
              <h6 class="mb-1 fw-bold">
                <span class="badge" [class]="badgeClass(n.type)">{{ n.type }}</span>
                {{ n.message || 'Notification' }}
              </h6>
              <small class="text-secondary">{{ n.createdAt | date:'short' }}</small>
            </div>
            <div class="d-flex gap-2">
              <button
                class="btn btn-sm btn-outline-primary"
                *ngIf="!n.isRead"
                (click)="markAsRead(n)"
              >
                Mark read
              </button>
              <button
                class="btn btn-sm btn-outline-secondary"
                *ngIf="n.isRead"
                (click)="markAsUnread(n)"
              >
                Mark unread
              </button>
              <button class="btn btn-sm btn-outline-danger" (click)="deleteNotification(n)">
                <i class="bi bi-trash">Delete</i>
              </button>
            </div>
          </div>
        </div>
      </div>

      <div *ngIf="!loading() && !filteredNotifications().length" class="alert alert-info mt-4" role="alert">
        <i class="bi bi-info-circle"></i>
        You're all caught up! No notifications to show.
      </div>

      <div class="d-flex align-items-center justify-content-between mt-3" *ngIf="totalPages() > 1">
        <button class="btn btn-outline-secondary btn-sm" (click)="prevPage()" [disabled]="page() === 0 || loading()">
          Previous
        </button>
        <div class="small text-muted">
          Page {{ page() + 1 }} of {{ totalPages() }} ({{ totalElements() }} total)
        </div>
        <button class="btn btn-outline-secondary btn-sm" (click)="nextPage()" [disabled]="page() >= totalPages() - 1 || loading()">
          Next
        </button>
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
    .border-left-warning {
      border-left: 4px solid var(--bs-warning) !important;
    }
  `],
})
export class NotificationListComponent implements OnInit {
  notifications = signal<Notification[]>([]);
  loading = signal(false);
  error = signal('');
  filter = signal<'all' | 'unread' | 'mentions'>('all');
  page = signal(0);
  pageSize = signal(10);
  totalPages = signal(0);
  totalElements = signal(0);

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.notificationService.notifications$.subscribe((items) => {
      this.notifications.set(items);
    });
    this.loadNotifications();
  }

  loadNotifications() {
    this.loading.set(true);
    this.error.set('');
    this.notificationService.getNotifications(this.page(), this.pageSize()).subscribe({
      next: (res) => {
        this.notifications.set(res?.content ?? []);
        this.page.set(res?.number ?? 0);
        this.pageSize.set(res?.size ?? this.pageSize());
        this.totalPages.set(res?.totalPages ?? 0);
        this.totalElements.set(res?.totalElements ?? 0);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Unable to load notifications.');
      }
    });
  }

  setFilter(value: 'all' | 'unread' | 'mentions') {
    this.filter.set(value);
  }

  nextPage() {
    if (this.page() < this.totalPages() - 1) {
      this.page.update((p) => p + 1);
      this.loadNotifications();
    }
  }

  prevPage() {
    if (this.page() > 0) {
      this.page.update((p) => p - 1);
      this.loadNotifications();
    }
  }

  filteredNotifications() {
    const all = this.notifications();
    const filter = this.filter();
    if (filter === 'unread') {
      return all.filter((n) => !n.isRead);
    }
    if (filter === 'mentions') {
      return all.filter((n) => n.type === NotificationType.MENTION);
    }
    return all;
  }

  badgeClass(type: Notification['type']) {
    switch (type) {
      case NotificationType.COMMENT:
        return 'bg-primary';
      case NotificationType.FOLLOW:
        return 'bg-success';
      case NotificationType.REACT:
        return 'bg-info';
      case NotificationType.MENTION:
        return 'bg-warning text-dark';
      case NotificationType.POST:
        return 'bg-warning text-dark';
      case NotificationType.REPORT:
        return 'bg-danger';
      default:
        return 'bg-secondary';
    }
  }

  markAsRead(notification: Notification) {
    this.notificationService.markAsRead(notification.id).subscribe({
      next: () => {
        this.notifications.set(
          this.notifications().map((n) =>
            n.id === notification.id ? { ...n, isRead: true } : n
          )
        );
      }
    });
  }

  markAsUnread(notification: Notification) {
    this.notificationService.markAsUnread(notification.id).subscribe({
      next: () => {
        this.notifications.set(
          this.notifications().map((n) =>
            n.id === notification.id ? { ...n, isRead: false } : n
          )
        );
      }
    });
  }

  deleteNotification(notification: Notification) {
    this.notificationService.deleteNotification(notification.id).subscribe({
      next: () => {
        this.notifications.set(this.notifications().filter((n) => n.id !== notification.id));
      }
    });
  }
}
