import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError, switchMap, of } from 'rxjs';
import { AuthService } from '@core/services/auth.service';

/**
 * HTTP Interceptor for global error handling with automatic token refresh
 */
export const errorInterceptor: HttpInterceptorFn = (req: any, next: any) => {
  const router = inject(Router);
  const auth = inject(AuthService);
  const isAuthRequest = req.url.includes('/auth/login')
    || req.url.includes('/auth/register')
    || req.url.includes('/auth/refresh')
    || req.url.includes('/auth/logout');

  return next(req).pipe(
    catchError((error: any) => {
      if (error.status === 401 && !isAuthRequest) {
        return auth.refresh().pipe(
          switchMap((res: any) => {
            const access = res?.data?.accessToken || res?.data?.accessToken;
            if (access) {
              auth.setToken(access);
              // retry original request with new token
              const cloned = req.clone({ setHeaders: { Authorization: `Bearer ${access}` } });
              return next(cloned);
            }
            router.navigate(['/auth/login']);
            return throwError(() => error);
          }),
          catchError((e) => {
            auth.clearToken();
            router.navigate(['/auth/login']);
            return throwError(() => e);
          })
        );
      } else if (error.status === 403) {
        router.navigate(['/']);
      } else if (error.status === 500) {
        console.error('Server error:', error);
      }

      return throwError(() => error);
    })
  );
};
