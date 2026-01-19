import { Routes } from '@angular/router';

export const REPORT_ROUTES: Routes = [
  {
    path: 'create',
    loadComponent: () => import('./components/report-create/report-create.component').then(m => m.ReportCreateComponent)
  }
];
