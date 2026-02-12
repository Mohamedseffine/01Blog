import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { catchError, map, of } from 'rxjs';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const token = authService.getToken();
  if (!token) {
    return of(router.createUrlTree(['/auth/login']));
  }

  return authService.getCurrentUser().pipe(
    map(() => true),
    catchError((err) => {
      if (err?.status === 401 || err?.status === 403) {
        authService.clearToken();
      }
      return of(router.createUrlTree(['/auth/login']));
    })
  );
};
