import { Routes } from '@angular/router';
import { adminGuard } from './domains/auth/guards/admin.guard';
import { authGuard } from './domains/auth/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./domains/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: 'error',
    loadComponent: () => import('./core/components/error-page/error-page.component').then(m => m.ErrorPageComponent)
  },
  {
    path: '',
    loadComponent: () => import('./layouts/main-layout/main-layout.component').then(m => m.MainLayoutComponent),
    canActivateChild: [authGuard],
    children: [
      {
        path: 'posts',
        loadChildren: () => import('./domains/post/post.routes').then(m => m.POST_ROUTES)
      },
      {
        path: 'comments',
        loadChildren: () => import('./domains/comment/comment.routes').then(m => m.COMMENT_ROUTES)
      },
      {
        path: 'users',
        loadChildren: () => import('./domains/user/user.routes').then(m => m.USER_ROUTES)
      },
      {
        path: 'notifications',
        loadChildren: () => import('./domains/notification/notification.routes').then(m => m.NOTIFICATION_ROUTES)
      },
      {
        path: 'admin',
        loadChildren: () => import('./domains/admin/admin.routes').then(m => m.ADMIN_ROUTES),
        canActivate: [adminGuard]
      },
      {
        path: 'reports',
        loadChildren: () => import('./domains/report/report.routes').then(m => m.REPORT_ROUTES)
      },
      {
        path: '',
        redirectTo: '/posts',
        pathMatch: 'full'
      },
      {
        path: '**',
        loadComponent: () => import('./core/components/not-found/not-found.component').then(m => m.NotFoundComponent)
      }
    ]
  }
];
