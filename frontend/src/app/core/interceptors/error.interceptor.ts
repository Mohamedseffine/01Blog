import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

/**
 * HTTP Interceptor for global error handling
 */
export const errorInterceptor: HttpInterceptorFn = (req: any, next: any) => {
  const router = inject(Router);

  return next(req).pipe(
    catchError(error => {
      if (error.status === 401) {
        // Unauthorized - redirect to login
        router.navigate(['/auth/login']);
      } else if (error.status === 403) {
        // Forbidden
        router.navigate(['/']);
      } else if (error.status === 500) {
        // Server error
        console.error('Server error:', error);
      }

      return throwError(() => error);
    })
  );
};
