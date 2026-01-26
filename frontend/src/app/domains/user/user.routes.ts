import { Routes } from '@angular/router';

export const USER_ROUTES: Routes = [
  {
    path: 'list',
    loadComponent: () => import('./components/user-list/user-list.component').then(m => m.UserListComponent)
  },
  {
    path: 'profile/:id',
    loadComponent: () => import('./components/user-profile/user-profile.component').then(m => m.UserProfileComponent)
  },
  {
    path: 'edit-profile',
    loadComponent: () => import('./components/edit-profile/edit-profile.component').then(m => m.EditProfileComponent)
  }
];
