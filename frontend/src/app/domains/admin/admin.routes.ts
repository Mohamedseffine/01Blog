import { Routes } from '@angular/router';

export const ADMIN_ROUTES: Routes = [
  {
    path: 'dashboard',
    loadComponent: () => import('./components/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent)
  },
  {
    path: 'users',
    loadComponent: () => import('./components/user-management/user-management.component').then(m => m.UserManagementComponent)
  },
  {
    path: 'posts',
    loadComponent: () => import('./components/post-management/post-management.component').then(m => m.PostManagementComponent)
  },
  {
    path: 'comments',
    loadComponent: () => import('./components/comment-management/comment-management.component').then(m => m.CommentManagementComponent)
  },
  {
    path: 'reports',
    loadComponent: () => import('./components/report-management/report-management.component').then(m => m.ReportManagementComponent)
  }
];
