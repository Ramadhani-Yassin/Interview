import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./pages/upload/upload.component').then(m => m.UploadComponent) },
  { path: 'uploads/:uploadId', loadComponent: () => import('./pages/records/records.component').then(m => m.RecordsComponent) },
];
