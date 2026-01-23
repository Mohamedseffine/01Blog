import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { catchError, map, of } from 'rxjs';

type JwtPayload = {
  role?: string;
};

const parseJwtPayload = (token: string): JwtPayload | null => {
  const parts = token.split('.');
  if (parts.length !== 3) return null;
  const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
  const padded = base64.padEnd(base64.length + (4 - (base64.length % 4)) % 4, '=');
  try {
    const json = atob(padded);
    return JSON.parse(json) as JwtPayload;
  } catch {
    return null;
  }
};

export const adminGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getToken();
  if (!token) {
    return router.createUrlTree(['/auth/login']);
  }

  const payload = parseJwtPayload(token);
  const role = payload?.role;
  if (role === 'ADMIN' || role === 'ROLE_ADMIN') {
    return true;
  }

  return authService.getCurrentUser().pipe(
    map((user) => {
      const isAdmin = user?.roles?.some((r) => r === 'ADMIN' || r === 'ROLE_ADMIN') ?? false;
      return isAdmin ? true : router.createUrlTree(['/posts']);
    }),
    catchError(() => of(router.createUrlTree(['/auth/login'])))
  );
};
