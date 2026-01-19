import { Routes } from '@angular/router';

export const POST_ROUTES: Routes = [
  {
    path: 'list',
    loadComponent: () => import('./components/post-list/post-list.component').then(m => m.PostListComponent)
  },
  {
    path: 'create',
    loadComponent: () => import('./components/post-create/post-create.component').then(m => m.PostCreateComponent)
  },
  {
    path: ':id',
    loadComponent: () => import('./components/post-detail/post-detail.component').then(m => m.PostDetailComponent)
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./components/post-edit/post-edit.component').then(m => m.PostEditComponent)
  },
  {
    path: '',
    redirectTo: 'list',
    pathMatch: 'full'
  }
];
