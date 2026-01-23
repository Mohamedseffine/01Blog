import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { AdminService } from '../../services/admin.service';
import { AdminUser, Page } from '../../models/admin.model';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="container py-4">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h1 class="mb-0">User Management</h1>
        <button class="btn btn-outline-secondary btn-sm" (click)="refresh()" [disabled]="loading()">
          <i class="bi bi-arrow-clockwise"></i> Refresh
        </button>
      </div>

      <div *ngIf="loading()" class="text-muted">Loading users...</div>
      <div *ngIf="error()" class="alert alert-danger">{{ error() }}</div>

      <div class="table-responsive" *ngIf="users().length">
        <table class="table table-hover align-middle">
          <thead>
            <tr>
              <th>User</th>
              <th>Email</th>
              <th>Role</th>
              <th>Status</th>
              <th>Ban Reason</th>
              <th>Duration Days</th>
              <th class="text-end">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let user of users()">
              <td>
                <div class="fw-semibold">{{ user.username }}</div>
                <div class="text-muted small">ID: {{ user.id }}</div>
              </td>
              <td>{{ user.email }}</td>
              <td>{{ user.role }}</td>
              <td>
                <span class="badge" [class.bg-danger]="user.banned" [class.bg-success]="!user.banned">
                  {{ user.banned ? 'Banned' : 'Active' }}
                </span>
              </td>
              <td>
                <input
                  class="form-control form-control-sm"
                  [(ngModel)]="banReason[user.id]"
                  placeholder="Reason"
                  [disabled]="user.banned"
                >
              </td>
              <td>
                <input
                  type="number"
                  class="form-control form-control-sm"
                  [(ngModel)]="banDuration[user.id]"
                  placeholder="Optional"
                  min="1"
                  [disabled]="user.banned"
                >
              </td>
              <td class="text-end">
                <a class="btn btn-sm btn-outline-primary me-2" [routerLink]="['/users/profile', user.id]">
                  View
                </a>
                <button
                  class="btn btn-sm btn-danger me-2"
                  (click)="ban(user)"
                  [disabled]="user.banned || !banReason[user.id]"
                >
                  Ban
                </button>
                <button
                  class="btn btn-sm btn-outline-success"
                  (click)="unban(user)"
                  [disabled]="!user.banned"
                >
                  Unban
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div *ngIf="!users().length && !loading()" class="text-muted">No users found.</div>

      <nav class="mt-4" *ngIf="pageInfo() as page">
        <ul class="pagination justify-content-center">
          <li class="page-item" [class.disabled]="page.first">
            <button class="page-link" (click)="changePage(page.number - 1)" [disabled]="page.first">Previous</button>
          </li>
          <li class="page-item disabled">
            <span class="page-link">Page {{ page.number + 1 }} of {{ page.totalPages }}</span>
          </li>
          <li class="page-item" [class.disabled]="page.last">
            <button class="page-link" (click)="changePage(page.number + 1)" [disabled]="page.last">Next</button>
          </li>
        </ul>
      </nav>
    </div>
  `,
})
export class UserManagementComponent implements OnInit {
  users = signal<AdminUser[]>([]);
  pageInfo = signal<Page<AdminUser> | null>(null);
  loading = signal(false);
  error = signal('');
  banReason: Record<number, string> = {};
  banDuration: Record<number, string> = {};

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh(page: number = 0) {
    this.loading.set(true);
    this.error.set('');
    this.adminService.getUsers(page, 10).subscribe({
      next: (res) => {
        const pageData = res.data;
        this.users.set(pageData.content || []);
        this.pageInfo.set(pageData);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Failed to load users.');
        this.loading.set(false);
      }
    });
  }

  changePage(page: number) {
    if (page < 0) return;
    this.refresh(page);
  }

  ban(user: AdminUser) {
    const reason = (this.banReason[user.id] || '').trim();
    if (!reason) return;
    const durationValue = this.banDuration[user.id];
    const durationDays = durationValue ? Number(durationValue) : undefined;
    const isPermanent = !durationDays;
    this.adminService.banUser(user.id, { reason, isPermanent, durationDays }).subscribe({
      next: () => {
        user.banned = true;
        this.banReason[user.id] = '';
        this.banDuration[user.id] = '';
      },
      error: () => {
        this.error.set(`Failed to ban ${user.username}.`);
      }
    });
  }

  unban(user: AdminUser) {
    this.adminService.unbanUser(user.id).subscribe({
      next: () => {
        user.banned = false;
      },
      error: () => {
        this.error.set(`Failed to unban ${user.username}.`);
      }
    });
  }
}
