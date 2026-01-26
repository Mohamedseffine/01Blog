import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError, switchMap, of, take } from 'rxjs';
import { AuthService } from '@core/services/auth.service';
import { ErrorService } from '@core/services/error.service';

/**
 * HTTP Interceptor for global error handling with automatic token refresh
 * Provides centralized error handling and user-friendly error messages
 */
export const errorInterceptor: HttpInterceptorFn = (req: any, next: any) => {
  const router = inject(Router);
  const auth = inject(AuthService);
  const errorService = inject(ErrorService);

  const isAuthRequest = req.url.includes('/auth/login')
    || req.url.includes('/auth/register')
    || req.url.includes('/auth/refresh')
    || req.url.includes('/auth/logout');
  const isAuthMe = req.url.includes('/auth/me');
  const isErrorPage = req.url.includes('/error');

  const redirectToError = (status: number, message?: string) => {
    const safeStatus = status || 500;
    const fallback =
      message ||
      (status === 401 ? 'Authentication required.'
        : status === 403 ? 'You do not have permission to perform this action.'
        : status === 404 ? 'Resource not found.'
        : status === 409 ? 'Conflict detected.'
        : status === 422 ? 'Validation failed.'
        : status === 0 ? 'Unable to connect to server.'
        : 'An unexpected error occurred.');
    router.navigate(['/error'], { queryParams: { status: safeStatus, message: fallback } });
  };

  return next(req).pipe(
    catchError((error: any) => {
      if (isErrorPage) {
        return throwError(() => error);
      }

      const status = error?.status;
      const serverMessage = error?.error?.message || error?.message;

      // Do not redirect on Bad Request
      if (status === 400) {
        const message = serverMessage || 'Please check your input and try again.';
        errorService.addError(message);
        return throwError(() => error);
      }

      // If /auth/me fails, try one refresh before treating it as logout
      if ((status === 401 || status === 403) && isAuthMe) {
        if (req.headers.has('X-Retry-Me')) {
          auth.clearToken();
          redirectToError(status, 'Your session has expired. Please log in again.');
          return throwError(() => error);
        }

        return auth.refresh().pipe(
          take(1),
          switchMap((res: any) => {
            const access = res?.data?.accessToken;
            if (access) {
              auth.setToken(access);
              const cloned = req.clone({
                setHeaders: {
                  Authorization: `Bearer ${access}`,
                  'X-Retry-Me': '1'
                }
              });
              return next(cloned);
            }
            auth.clearToken();
            redirectToError(status, 'Your session has expired. Please log in again.');
            return throwError(() => error);
          }),
          catchError((refreshError) => {
            auth.clearToken();
            redirectToError(status, 'Authentication failed. Please log in again.');
            return throwError(() => refreshError);
          })
        );
      }

      // Handle 401 Unauthorized - Try to refresh token (but not for auth requests to prevent infinite loop)
      if (status === 401 && !isAuthRequest) {
        const currentToken = auth.getToken();

        // If there's no token, don't try to refresh - just logout
        if (!currentToken) {
          auth.logout().subscribe({ error: () => undefined });
          redirectToError(status, 'Please log in to continue.');
          return throwError(() => error);
        }

        // Check if we already tried to refresh to prevent infinite loop
        if (req.headers.has('X-Retry-Refresh')) {
          auth.clearToken();
          redirectToError(status, 'Your session has expired. Please log in again.');
          return throwError(() => error);
        }

        return auth.refresh().pipe(
          take(1),
          switchMap((res: any) => {
            const access = res?.data?.accessToken;
            if (access) {
              auth.setToken(access);
              // Retry original request with new token
              const cloned = req.clone({ setHeaders: { Authorization: `Bearer ${access}` } });
              return next(cloned);
            }
            // Refresh didn't return token - clear auth and redirect
            auth.clearToken();
            redirectToError(status, 'Your session has expired. Please log in again.');
            return throwError(() => error);
          }),
          catchError((refreshError) => {
            // If refresh fails, clear auth and redirect
            auth.clearToken();
            redirectToError(status, 'Authentication failed. Please log in again.');
            return throwError(() => refreshError);
          })
        );
      }

      // All other errors redirect to error page with status and message
      redirectToError(status, serverMessage);
      return throwError(() => error);
    })
  );
};
