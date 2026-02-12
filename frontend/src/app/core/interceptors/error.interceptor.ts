import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '@core/services/auth.service';
import { ErrorService } from '@core/services/error.service';

/**
 * HTTP Interceptor for global error handling
 * Provides centralized error handling and user-friendly error messages
 */
export const errorInterceptor: HttpInterceptorFn = (req: any, next: any) => {
  const router = inject(Router);
  const auth = inject(AuthService);
  const errorService = inject(ErrorService);
  
  const isAuthRequest = req.url.includes('/auth/login')
    || req.url.includes('/auth/register')
    || req.url.includes('/auth/logout');
  const isAuthMe = req.url.includes('/auth/me');

  return next(req).pipe(
    catchError((error: any) => {
      // If /auth/me fails, treat as unauthenticated
      if ((error.status === 401 || error.status === 403) && isAuthMe) {
        auth.clearToken();
        router.navigate(['/auth/login']);
        errorService.addError('Your session has expired. Please log in again.');
        return throwError(() => error);
      }

      // Handle 401 Unauthorized for protected routes
      if (error.status === 401 && !isAuthRequest) {
        const hadToken = !!auth.getToken();
        auth.clearToken();
        router.navigate(['/auth/login']);
        errorService.addError(hadToken
          ? 'Your session has expired. Please log in again.'
          : 'Please log in to continue.');
        return throwError(() => error);
      }
      
      // Handle 403 Forbidden - especially on /auth/me, treat as 401
      if (error.status === 403) {
        errorService.addError('You do not have permission to perform this action.');
        router.navigate(['/']);
        return throwError(() => error);
      }
      
      // Handle 404 Not Found
      if (error.status === 404) {
        if (req.url.includes('/profile-picture')) {
          // Silent 404 for missing profile images; let callers decide how to handle
          return throwError(() => error);
        }
        if (req.url.includes('/posts/') || req.url.includes('/comments')) {
          router.navigate(['/not-found']);
          return throwError(() => error);
        }
        errorService.addError('Resource not found.');
        return throwError(() => error);
      }
      
      // Handle 409 Conflict
      if (error.status === 409) {
        const message = error.error?.message || 'This action conflicts with existing data.';
        errorService.addError(message);
        return throwError(() => error);
      }
      
      // Handle 422 Unprocessable Entity (Validation errors)
      if (error.status === 422) {
        const message = error.error?.message || 'Please check your input and try again.';
        errorService.addError(message);
        return throwError(() => error);
      }
      
      // Handle 500 Server Error
      if (error.status >= 500) {
        errorService.addError('Server error. Please try again later.');
        return throwError(() => error);
      }
      
      // Handle network errors (status 0)
      if (error.status === 0) {
        errorService.addError('Unable to connect to server. Please check your internet connection.');
        return throwError(() => error);
      }
      
      // Handle other errors with generic message
      const errorMessage = error.error?.message || 'An unexpected error occurred. Please try again.';
      if (error.status >= 400 && error.status < 500) {
        errorService.addError(errorMessage);
      }
      
      return throwError(() => error);
    })
  );
};
