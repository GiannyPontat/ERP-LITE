import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'onboarding',
    loadComponent: () => import('./features/onboarding/onboarding.component').then(m => m.OnboardingComponent),
    canActivate: [authGuard],
    title: 'Bienvenue - Configuration'
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [authGuard],
    title: 'Mon activité'
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
    path: 'catalog',
    loadChildren: () => import('./features/catalog/catalog.routes').then(m => m.CATALOG_ROUTES),
    canActivate: [authGuard]
  },
  {
    path: 'interventions',
    loadChildren: () => import('./features/interventions/interventions.routes').then(m => m.INTERVENTIONS_ROUTES),
    canActivate: [authGuard],
    title: 'Mes interventions'
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  // Redirections temporaires pour routes non implémentées
  // TODO: Implémenter ces pages dans une prochaine version
  {
    path: 'client-portal',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'settings',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'help',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: '/dashboard'
  }
];
