import { Routes } from '@angular/router';

export const CATALOG_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./catalog-list/catalog-list.component').then(m => m.CatalogListComponent),
    title: 'Catalogue BTP'
  },
  {
    path: 'new',
    loadComponent: () => import('./catalog-form/catalog-form.component').then(m => m.CatalogFormComponent),
    title: 'Nouvel article'
  },
  {
    path: ':id',
    loadComponent: () => import('./catalog-detail/catalog-detail.component').then(m => m.CatalogDetailComponent),
    title: 'Détail article'
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./catalog-form/catalog-form.component').then(m => m.CatalogFormComponent),
    title: 'Modifier article'
  }
];

