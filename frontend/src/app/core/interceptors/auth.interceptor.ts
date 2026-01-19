import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '@domains/auth/services/auth.service';

/**
 * HTTP Interceptor for attaching JWT token to all requests
 */
export const authInterceptor: HttpInterceptorFn = (req: any, next: any) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  if (token && !req.url.includes('/auth/')) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req);
};
