import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface AppError {
  message: string;
  type: 'error' | 'warning' | 'info' | 'success';
  code?: string;
  timestamp: Date;
  dismissible?: boolean;
  duration?: number; // in ms, 0 = persistent
}

/**
 * Global Error Service for handling and displaying errors throughout the application
 * Provides a centralized way to manage error notifications
 */
@Injectable({
  providedIn: 'root'
})
export class ErrorService {
  private errorsSubject = new BehaviorSubject<AppError[]>([]);
  private recentKeys = new Map<string, number>();
  public errors$ = this.errorsSubject.asObservable();

  constructor() {}

  /**
   * Add an error to the error queue
   */
  addError(message: string, code?: string, duration: number = 5000): void {
    const error: AppError = {
      message,
      type: 'error',
      code,
      timestamp: new Date(),
      dismissible: true,
      duration
    };
    this.addAppError(error);
  }

  /**
   * Add a warning to the error queue
   */
  addWarning(message: string, duration: number = 4000): void {
    const error: AppError = {
      message,
      type: 'warning',
      timestamp: new Date(),
      dismissible: true,
      duration
    };
    this.addAppError(error);
  }

  /**
   * Add a success message to the error queue
   */
  addSuccess(message: string, duration: number = 3000): void {
    const error: AppError = {
      message,
      type: 'success',
      timestamp: new Date(),
      dismissible: true,
      duration
    };
    this.addAppError(error);
  }

  /**
   * Add an info message to the error queue
   */
  addInfo(message: string, duration: number = 3000): void {
    const error: AppError = {
      message,
      type: 'info',
      timestamp: new Date(),
      dismissible: true,
      duration
    };
    this.addAppError(error);
  }

  /**
   * Add a custom app error
   */
  addAppError(error: AppError): void {
    const now = Date.now();
    const key = `${error.type}|${error.message}`.trim();
    const last = this.recentKeys.get(key);

    if (last && now - last < 2000) {
      return;
    }

    this.recentKeys.set(key, now);
    setTimeout(() => {
      // Only clear if no newer entry has overwritten this timestamp
      if (this.recentKeys.get(key) === now) {
        this.recentKeys.delete(key);
      }
    }, 4000);

    const errors = this.errorsSubject.value;
    error.timestamp = error.timestamp ?? new Date();
    this.errorsSubject.next([...errors, error]);

    // Auto-dismiss after duration
    if (error.duration && error.duration > 0) {
      setTimeout(() => this.dismissError(error), error.duration);
    }
  }

  /**
   * Dismiss a specific error
   */
  dismissError(error: AppError): void {
    const errors = this.errorsSubject.value.filter(e => e.timestamp !== error.timestamp);
    this.errorsSubject.next(errors);
  }

  /**
   * Clear all errors
   */
  clearErrors(): void {
    this.errorsSubject.next([]);
  }

  /**
   * Get human-readable error message from HTTP error
   */
  getErrorMessage(error: any): string {
    if (error.status === 0) {
      return 'Unable to connect to server. Please check your internet connection.';
    }
    if (error.status === 400) {
      return error.error?.message || 'Invalid request. Please check your input.';
    }
    if (error.status === 401) {
      return 'Your session has expired. Please log in again.';
    }
    if (error.status === 403) {
      return 'You do not have permission to perform this action.';
    }
    if (error.status === 404) {
      return 'Resource not found.';
    }
    if (error.status === 409) {
      return error.error?.message || 'This action conflicts with existing data.';
    }
    if (error.status >= 500) {
      return 'Server error. Please try again later.';
    }
    if (error.error?.message) {
      return error.error.message;
    }
    return 'An unexpected error occurred. Please try again.';
  }
}
