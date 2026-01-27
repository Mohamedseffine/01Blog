import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AdminService } from '../../services/admin.service';
import { AdminReport, Page } from '../../models/admin.model';
import { DebounceClickDirective } from '@shared/directives/debounce-click.directive';

@Component({
  selector: 'app-report-management',
  standalone: true,
  imports: [CommonModule, DebounceClickDirective],
  template: `
    <div class="container py-4">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h1 class="mb-0">Reports</h1>
        <button class="btn btn-outline-secondary btn-sm" (click)="refresh()" [disabled]="loading()">
          <i class="bi bi-arrow-clockwise"></i> Refresh
        </button>
      </div>

      <div *ngIf="loading()" class="text-muted">Loading reports...</div>
      <div *ngIf="error()" class="alert alert-danger">{{ error() }}</div>

      <div class="table-responsive" *ngIf="reports().length">
        <table class="table table-hover align-middle">
          <thead>
            <tr>
              <th>ID</th>
              <th>Type</th>
              <th>Reason</th>
              <th>Status</th>
              <th>Reporter</th>
              <th>Reported</th>
              <th>Created</th>
              <th class="text-end">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let report of reports()">
              <td>{{ report.id }}</td>
              <td>{{ report.contentType }} {{ report.contentId ? '#' + report.contentId : '' }}</td>
              <td>{{ report.reason }}</td>
              <td>
                <span class="badge"
                  [class.bg-warning]="report.status === 'PENDING'"
                  [class.bg-info]="report.status === 'UNDER_REVIEW'"
                  [class.bg-success]="report.status === 'RESOLVED'"
                  [class.bg-secondary]="report.status === 'DISMISSED'">
                  {{ report.status }}
                </span>
              </td>
              <td>{{ report.reporterUsername || 'Unknown' }}</td>
              <td>{{ report.reportedUsername || 'N/A' }}</td>
              <td>{{ report.createdAt | date:'short' }}</td>
              <td class="text-end">
                <button
                  class="btn btn-sm btn-outline-success me-2"
                  appDebounceClick
                  (appDebounceClick)="resolve(report)"
                  [disabled]="report.status === 'RESOLVED'"
                >
                  Resolve
                </button>
                <button
                  class="btn btn-sm btn-outline-danger"
                  *ngIf="report.contentType === 'POST' && report.contentId"
                  appDebounceClick
                  (appDebounceClick)="deletePost(report)"
                >
                  Delete Post
                </button>
                <button
                  class="btn btn-sm btn-outline-danger"
                  *ngIf="report.contentType === 'COMMENT' && report.contentId"
                  appDebounceClick
                  (appDebounceClick)="deleteComment(report)"
                >
                  Delete Comment
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div *ngIf="!reports().length && !loading()" class="text-muted">No reports found.</div>

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
export class ReportManagementComponent implements OnInit {
  reports = signal<AdminReport[]>([]);
  pageInfo = signal<Page<AdminReport> | null>(null);
  loading = signal(false);
  error = signal('');

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh(page: number = 0) {
    this.loading.set(true);
    this.error.set('');
    this.adminService.getReports(page, 10).subscribe({
      next: (res) => {
        const pageData = res.data;
        this.reports.set(pageData.content || []);
        this.pageInfo.set(pageData);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Failed to load reports.');
        this.loading.set(false);
      }
    });
  }

  changePage(page: number) {
    if (page < 0) return;
    this.refresh(page);
  }

  resolve(report: AdminReport) {
    this.adminService.resolveReport(report.id).subscribe({
      next: () => {
        this.removeReports((r) => r.id === report.id);
      },
      error: () => {
        this.error.set(`Failed to resolve report #${report.id}.`);
      }
    });
  }

  deletePost(report: AdminReport) {
    if (!report.contentId) return;
    this.adminService.deletePost(report.contentId).subscribe({
      next: () => {
        this.removeReports((r) => r.contentType === 'POST' && r.contentId === report.contentId);
      },
      error: () => {
        this.error.set(`Failed to delete post for report #${report.id}.`);
      }
    });
  }

  deleteComment(report: AdminReport) {
    if (!report.contentId) return;
    this.adminService.deleteComment(report.contentId).subscribe({
      next: () => {
        this.removeReports((r) => r.contentType === 'COMMENT' && r.contentId === report.contentId);
      },
      error: () => {
        this.error.set(`Failed to delete comment for report #${report.id}.`);
      }
    });
  }

  private removeReports(predicate: (r: AdminReport) => boolean) {
    const current = this.reports();
    const remaining = current.filter((r) => !predicate(r));
    const removedCount = current.length - remaining.length;
    this.reports.set(remaining);

    const page = this.pageInfo();
    if (page && removedCount > 0) {
      const totalElements = Math.max(0, (page.totalElements ?? current.length) - removedCount);
      const totalPages = page.size ? Math.max(1, Math.ceil(totalElements / page.size)) : page.totalPages;
      this.pageInfo.set({
        ...page,
        totalElements,
        totalPages,
        first: page.number === 0,
        last: page.number + 1 >= totalPages
      });
    }
  }
}
