import { HttpEvent, HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { tap } from 'rxjs/operators';
import { ErrorService } from '@core/services/error.service';

/**
 * Success interceptor: shows a toast for non-GET successful requests
 * Skips auth endpoints and limits to user-facing mutations.
 */
export const successInterceptor: HttpInterceptorFn = (req, next) => {
  const errorService = inject(ErrorService);
  const method = req.method?.toUpperCase?.() || '';
  const isReadOnly = method === 'GET' || method === 'HEAD';
  const url = req.url || '';

  const isAuthEndpoint =
    url.includes('/auth/login') ||
    url.includes('/auth/register') ||
    url.includes('/auth/logout');

  if (isReadOnly || isAuthEndpoint) {
    return next(req);
  }

  const deriveMessage = (body: any): string => {
    if (body?.message) return body.message;
    if (body?.data && typeof body.data === 'string') return body.data;

    if (method === 'POST' && url.includes('/posts')) return 'Post created successfully.';
    if (method === 'PUT' && url.includes('/posts')) return 'Post updated successfully.';
    if (method === 'DELETE' && url.includes('/posts')) return 'Post deleted successfully.';

    if (method === 'POST' && url.includes('/comments')) return 'Comment created successfully.';
    if (method === 'PUT' && url.includes('/comments')) return 'Comment updated successfully.';
    if (method === 'DELETE' && url.includes('/comments')) return 'Comment deleted successfully.';

    if (method === 'POST' && url.includes('/reacts')) return 'Reaction saved successfully.';
    if (method === 'DELETE' && url.includes('/reacts')) return 'Reaction removed successfully.';

    if (method === 'POST' && url.includes('/reports')) return 'Report submitted successfully.';
    if (method === 'PUT' && url.includes('/reports/') && url.includes('/resolve')) return 'Report resolved successfully.';

    if (method === 'PUT' && url.includes('/users/current')) return 'Profile updated successfully.';
    if (method === 'PUT' && /\/users\/\d+$/.test(url)) return 'User updated successfully.';
    if (method === 'DELETE' && /\/users\/\d+$/.test(url)) return 'User deleted successfully.';

    if (url.includes('/admin/users') && url.includes('/ban')) return 'User banned successfully.';
    if (url.includes('/admin/users') && url.includes('/unban')) return 'User unbanned successfully.';
    if (url.includes('/admin/comments') && url.includes('/hide')) return 'Comment hidden successfully.';
    if (url.includes('/admin/comments') && url.includes('/unhide')) return 'Comment unhidden successfully.';
    if (url.includes('/admin/posts') && url.includes('/hide')) return 'Post hidden successfully.';
    if (url.includes('/admin/posts') && url.includes('/unhide')) return 'Post unhidden successfully.';

    return 'Action completed successfully.';
  };

  return next(req).pipe(
    tap({
      next: (event: HttpEvent<any>) => {
        if (!(event instanceof HttpResponse)) return;
        const message = deriveMessage(event.body);
        if (message) {
          errorService.addSuccess(message);
        }
      },
      error: () => {
        // errors handled by error interceptor
      }
    })
  );
};
