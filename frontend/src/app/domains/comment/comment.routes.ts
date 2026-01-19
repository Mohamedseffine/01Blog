import { Routes } from '@angular/router';

export const COMMENT_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/comment-list/comment-list.component').then(m => m.CommentListComponent)
  }
];
