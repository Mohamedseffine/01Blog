import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { animate, style, transition, trigger } from '@angular/animations';
import { ErrorService, AppError } from '@core/services/error.service';

/**
 * Error Alert Component - Displays application errors as dismissible toasts
 * Shows errors, warnings, success, and info messages with auto-dismiss functionality
 * Positioned at top of page with Bootstrap styling and animations
 */
@Component({
  selector: 'app-error-alert',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="error-container">
      <div
        *ngFor="let error of errors$ | async; let i = index"
        @fadeInOut
        class="alert"
        [ngClass]="'alert-' + getAlertClass(error.type)"
        role="alert"
      >
        <div class="alert-content">
          <span class="alert-icon">{{ getAlertIcon(error.type) }}</span>
          <span class="alert-message">{{ error.message }}</span>
          <button
            *ngIf="error.dismissible !== false"
            type="button"
            class="btn-close"
            aria-label="Close"
            (click)="dismissErrorByIndex(i, error)"
          ></button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .error-container {
      position: fixed;
      top: 96px;
      right: 20px;
      z-index: 20000;
      max-width: 400px;
      display: flex;
      flex-direction: column;
      gap: 10px;
      pointer-events: auto;
    }

    .alert {
      display: flex;
      align-items: center;
      padding: 12px 16px;
      border-radius: 4px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
      font-size: 14px;
      line-height: 1.5;
      margin-bottom: 0;
    }

    .alert-content {
      display: flex;
      align-items: center;
      width: 100%;
      gap: 10px;
    }

    .alert-icon {
      flex-shrink: 0;
      font-weight: bold;
      font-size: 16px;
      display: inline-flex;
      align-items: center;
    }

    .alert-message {
      flex-grow: 1;
      word-break: break-word;
    }

    .btn-close {
      flex-shrink: 0;
      margin-left: auto;
      padding: 0;
      background: transparent;
      border: 0;
      font-size: 20px;
      cursor: pointer;
      opacity: 0.7;
      transition: opacity 0.2s;
    }

    .btn-close:hover {
      opacity: 1;
    }

    /* Bootstrap Alert Classes */
    .alert-danger {
      background-color: #f8d7da;
      color: #721c24;
      border: 1px solid #f5c6cb;
    }

    .alert-warning {
      background-color: #fff3cd;
      color: #856404;
      border: 1px solid #ffeeba;
    }

    .alert-success {
      background-color: #d4edda;
      color: #155724;
      border: 1px solid #c3e6cb;
    }

    .alert-info {
      background-color: #d1ecf1;
      color: #0c5460;
      border: 1px solid #bee5eb;
    }

    /* Media Query for Mobile */
    @media (max-width: 576px) {
      .error-container {
        right: 10px;
        left: 10px;
        max-width: none;
      }

      .alert {
        padding: 10px 12px;
        font-size: 13px;
      }
    }
  `],
  animations: [
    trigger('fadeInOut', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateX(400px)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'translateX(0)' }))
      ]),
      transition(':leave', [
        animate('300ms ease-in', style({ opacity: 0, transform: 'translateX(400px)' }))
      ])
    ])
  ]
})
export class ErrorAlertComponent implements OnInit {
  errors$ = this.errorService.errors$;

  constructor(public errorService: ErrorService) {}

  ngOnInit(): void {
    // Component initialization if needed
  }

  /**
   * Dismiss error by finding and removing it
   */
  dismissErrorByIndex(index: number, error: AppError): void {
    this.errorService.dismissError(error);
  }

  /**
   * Maps error type to Bootstrap alert class
   */
  getAlertClass(type: 'error' | 'warning' | 'info' | 'success'): string {
    const classMap: Record<string, string> = {
      'error': 'danger',
      'warning': 'warning',
      'success': 'success',
      'info': 'info'
    };
    return classMap[type] || 'info';
  }

  /**
   * Gets emoji icon for error type
   */
  getAlertIcon(type: 'error' | 'warning' | 'info' | 'success'): string {
    const iconMap: Record<string, string> = {
      'error': '❌',
      'warning': '⚠️',
      'success': '✅',
      'info': 'ℹ️'
    };
    return iconMap[type] || 'ℹ️';
  }
}
