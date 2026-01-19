import { Routes } from '@angular/router';

export const NOTIFICATION_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/notification-list/notification-list.component').then(m => m.NotificationListComponent)
  }
];
