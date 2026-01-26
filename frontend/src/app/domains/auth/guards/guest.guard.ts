import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { catchError, map, of } from 'rxjs';

export const guestGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const token = authService.getToken();
  if (!token) {
    return of(true);
  }

  return authService.getCurrentUser().pipe(
    map(() => router.createUrlTree(['/'])),
    catchError(() => of(true))
  );
};
