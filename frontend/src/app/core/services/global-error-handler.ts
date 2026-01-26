import { ErrorHandler, Injectable, Injector } from '@angular/core';
import { ErrorService } from './error.service';

/**
 * Custom Angular Error Handler
 * Intercepts all Angular errors and determines whether to display or silently handle them
 * Prevents console spam from known non-critical errors
 */
@Injectable({
  providedIn: 'root'
})
export class GlobalErrorHandler implements ErrorHandler {
  constructor(private injector: Injector) {}

  handleError(error: Error | any): void {
    const errorService = this.injector.get(ErrorService);
    const errorMessage = this.extractErrorMessage(error);
    const errorType = this.classifyError(error);

    // Known errors to silently handle (not shown to user)
    if (this.shouldSilenceError(error, errorMessage)) {
      this.logSilentError(errorMessage, error);
      return;
    }

    // Critical errors shown to user
    if (errorType === 'critical') {
      errorService.addError(errorMessage);
      console.error('[CRITICAL ERROR]', error);
    } else if (errorType === 'warning') {
      // Non-critical but important errors shown as warning
      errorService.addWarning(errorMessage);
      console.warn('[WARNING]', error);
    } else if (errorType === 'transient') {
      // Network/transient errors logged only in debug
      console.debug('[TRANSIENT ERROR]', errorMessage);
    }
  }

  /**
   * Determine if error should be silenced
   */
  private shouldSilenceError(error: Error | any, message: string): boolean {
    const knownSilentErrors: string[] = [
      'WebSocket',
      'STOMP',
      'subscribe error',
      'Publish error',
      'Connection lost',
      'Disconnected',
      'Network request failed',
      'Failed to fetch',
      'timeout',
      'top.GLOBALS',
      'chrome-extension',
      'moz-extension',
      'ResizeObserver loop limit exceeded',
      'Non-Error promise rejection',
      'Cancel',
      'Canceled',
      'Aborted',
      'deadCode',
      'document is not defined',
      'common not found',
      'Warning',
    ];

    const errorStr = `${message}${error?.stack || ''}`.toLowerCase();
    return knownSilentErrors.some(pattern =>
      errorStr.includes(pattern.toLowerCase())
    );
  }

  /**
   * Classify error severity
   */
  private classifyError(error: Error | any): 'critical' | 'warning' | 'transient' {
    const message = this.extractErrorMessage(error);
    const status = error?.status;

    // Critical errors
    if (
      message.includes('401') ||
      message.includes('403') ||
      message.includes('Authentication') ||
      message.includes('Authorization') ||
      message.includes('Unauthorized')
    ) {
      return 'critical';
    }

    // HTTP 500+ errors
    if (status && status >= 500) {
      return 'critical';
    }

    // Warning level errors
    if (
      message.includes('404') ||
      message.includes('409') ||
      message.includes('Conflict') ||
      message.includes('validation')
    ) {
      return 'warning';
    }

    // Transient/network errors
    if (
      message.includes('timeout') ||
      message.includes('Network') ||
      message.includes('connection')
    ) {
      return 'transient';
    }

    // Default to warning
    return 'warning';
  }

  /**
   * Extract readable error message
   */
  private extractErrorMessage(error: Error | any): string {
    if (!error) {
      return 'Unknown error occurred';
    }

    if (error instanceof Error) {
      return error.message;
    }

    if (typeof error === 'string') {
      return error;
    }

    if (error?.message) {
      return error.message;
    }

    if (error?.error?.message) {
      return error.error.message;
    }

    if (error?.statusText) {
      return error.statusText;
    }

    return JSON.stringify(error).substring(0, 100);
  }

  /**
   * Log silent errors to console only in development
   */
  private logSilentError(message: string, error: any): void {
    // Only log in development/debug mode
    const isDev = !this.isProduction();
    if (isDev) {
      console.debug('[SILENCED ERROR]', message, error);
    }
  }

  /**
   * Check if running in production
   */
  private isProduction(): boolean {
    return (
      typeof window !== 'undefined' &&
      window.location.hostname !== 'localhost' &&
      window.location.hostname !== '127.0.0.1'
    );
  }
}
