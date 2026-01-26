import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '@core/services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const token = authService.getToken();
  const skipAuthEndpoints = ['/auth/login', '/auth/register', '/auth/refresh', '/auth/logout', '/auth/me'];
  const isSkipAuth = skipAuthEndpoints.some(e => req.url.includes(e));

  if (token && !isSkipAuth) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }

  return next(req).pipe(
    catchError(err => {
      const isAuthEndpoint = skipAuthEndpoints.some(e => req.url.includes(e));
      if ((err.status === 401 || err.status === 403) && !isAuthEndpoint) {
        authService.clearToken();
        router.navigate(['/auth/login']);
      }
      return throwError(() => err);
    })
  );
};
