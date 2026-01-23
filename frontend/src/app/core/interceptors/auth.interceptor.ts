import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '@core/services/auth.service';

/**
 * HTTP Interceptor for attaching JWT token to all requests
 */
export const authInterceptor: HttpInterceptorFn = (req: any, next: any) => {
  const authService = inject(AuthService);
  const token = authService.getToken();
  const skipAuthEndpoints = ['/auth/login', '/auth/register', '/auth/refresh', '/auth/logout'];
  const isSkipAuth = skipAuthEndpoints.some((endpoint) => req.url.includes(endpoint));

  if (token && !isSkipAuth) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req);
};
