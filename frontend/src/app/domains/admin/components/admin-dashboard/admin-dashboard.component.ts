import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { AdminService } from '../../services/admin.service';
import { AdminDashboard } from '../../models/admin.model';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="container py-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="mb-0">Admin Dashboard</h1>
        <button class="btn btn-outline-secondary btn-sm" (click)="refresh()" [disabled]="loading()">
          <i class="bi bi-arrow-clockwise"></i> Refresh
        </button>
      </div>

      <div *ngIf="loading()" class="text-muted">Loading dashboard...</div>
      <div *ngIf="error()" class="alert alert-danger">{{ error() }}</div>

      <div class="row g-3" *ngIf="stats() as data">
        <div class="col-6 col-lg-4">
          <div class="card shadow-sm h-100">
            <div class="card-body">
              <div class="text-muted small">Total Users</div>
              <div class="fs-2 fw-bold">{{ data.totalUsers }}</div>
            </div>
          </div>
        </div>
        <div class="col-6 col-lg-4">
          <div class="card shadow-sm h-100">
            <div class="card-body">
              <div class="text-muted small">Total Posts</div>
              <div class="fs-2 fw-bold">{{ data.totalPosts }}</div>
            </div>
          </div>
        </div>
        <div class="col-6 col-lg-4">
          <div class="card shadow-sm h-100">
            <div class="card-body">
              <div class="text-muted small">Total Comments</div>
              <div class="fs-2 fw-bold">{{ data.totalComments }}</div>
            </div>
          </div>
        </div>
        <div class="col-6 col-lg-4">
          <div class="card shadow-sm h-100">
            <div class="card-body">
              <div class="text-muted small">Total Reports</div>
              <div class="fs-2 fw-bold">{{ data.totalReports }}</div>
            </div>
          </div>
        </div>
        <div class="col-6 col-lg-4">
          <div class="card shadow-sm h-100">
            <div class="card-body">
              <div class="text-muted small">Pending Reports</div>
              <div class="fs-2 fw-bold text-warning">{{ data.pendingReports }}</div>
            </div>
          </div>
        </div>
        <div class="col-6 col-lg-4">
          <div class="card shadow-sm h-100">
            <div class="card-body">
              <div class="text-muted small">Banned Users</div>
              <div class="fs-2 fw-bold text-danger">{{ data.bannedUsers }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="card shadow-sm mt-4" *ngIf="stats()">
        <div class="card-body">
          <h5 class="card-title">Quick Actions</h5>
          <div class="d-flex flex-wrap gap-2">
            <a class="btn btn-primary" routerLink="/admin/users">
              <i class="bi bi-people"></i> Manage Users
            </a>
            <a class="btn btn-outline-primary" routerLink="/admin/reports">
              <i class="bi bi-flag"></i> Review Reports
            </a>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class AdminDashboardComponent implements OnInit {
  stats = signal<AdminDashboard | null>(null);
  loading = signal(false);
  error = signal('');

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh() {
    this.loading.set(true);
    this.error.set('');
    this.adminService.getDashboard().subscribe({
      next: (res) => {
        this.stats.set(res.data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Failed to load dashboard data.');
        this.loading.set(false);
      }
    });
  }
}
