import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [authGuard]
  },
  {
    path: 'clients',
    loadChildren: () => import('./features/clients/clients.routes').then(m => m.CLIENTS_ROUTES),
    canActivate: [authGuard]
  },
  {
    path: 'quotes',
    loadChildren: () => import('./features/quotes/quotes.routes').then(m => m.QUOTES_ROUTES),
    canActivate: [authGuard]
  },
  {
    path: 'invoices',
    loadChildren: () => import('./features/invoices/invoices.routes').then(m => m.INVOICES_ROUTES),
    canActivate: [authGuard]
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: '**',
    redirectTo: '/dashboard'
  }
];
