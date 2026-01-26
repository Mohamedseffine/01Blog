import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { catchError, map, of, switchMap, take } from 'rxjs';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const refreshAndRetry = () => authService.refresh().pipe(
    take(1),
    switchMap((res: any) => {
      const access = res?.data?.accessToken;
      if (access) {
        authService.setToken(access);
        return authService.getCurrentUser().pipe(
          map(() => true),
          catchError(() => of(router.createUrlTree(['/auth/login'])))
        );
      }
      authService.clearToken();
      return of(router.createUrlTree(['/auth/login']));
    }),
    catchError(() => {
      authService.clearToken();
      return of(router.createUrlTree(['/auth/login']));
    })
  );

  const token = authService.getToken();
  if (!token) {
    return refreshAndRetry();
  }

  return authService.getCurrentUser().pipe(
    map(() => true),
    catchError((err) => {
      if (err?.status === 401 || err?.status === 403) {
        return refreshAndRetry();
      }
      return of(router.createUrlTree(['/auth/login']));
    })
  );
};
