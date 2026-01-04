# Structure Angular 17+ - ERP-LITE Frontend

## ✅ Structure créée

```
src/app/
├── core/                    # Modules core (singletons)
│   ├── guards/             # Route guards
│   ├── interceptors/       # HTTP interceptors
│   ├── services/           # Services globaux
│   └── models/             # Modèles de données
│
├── shared/                  # Composants et directives partagés
│   ├── components/         # Composants réutilisables
│   └── directives/         # Directives personnalisées
│
└── features/                # Modules fonctionnels (lazy loading)
    ├── auth/               # Authentification
    │   ├── login/
    │   ├── register/
    │   └── auth.routes.ts
    │
    ├── dashboard/          # Tableau de bord
    │   └── dashboard.component.ts
    │
    ├── clients/            # Gestion des clients
    │   ├── clients-list/
    │   ├── client-form/
    │   ├── client-detail/
    │   └── clients.routes.ts
    │
    ├── quotes/             # Gestion des devis
    │   ├── quotes-list/
    │   ├── quote-form/
    │   ├── quote-detail/
    │   └── quotes.routes.ts
    │
    └── invoices/           # Gestion des factures
        ├── invoices-list/
        ├── invoice-form/
        ├── invoice-detail/
        └── invoices.routes.ts
```

---

## ✅ Configuration

### Environment
**Fichier:** `src/environments/environment.ts`
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1'
};
```

**Fichier:** `src/environments/environment.prod.ts`
```typescript
export const environment = {
  production: true,
  apiUrl: 'http://localhost:8080/api/v1'
};
```

### App Config
**Fichier:** `src/app/app.config.ts`
- ✅ Router configuré
- ✅ Animations Material configurées
- ✅ HTTP Client configuré

---

## ✅ AppComponent avec Material

### Composants Material utilisés
- `MatToolbar` - Barre de navigation principale
- `MatSidenav` - Menu latéral
- `MatList` - Liste de navigation
- `MatIcon` - Icônes Material
- `MatButton` - Boutons Material
- `MatMenu` - Menu utilisateur

### Fonctionnalités
- ✅ Sidebar responsive (mobile/desktop)
- ✅ Menu de navigation avec icônes
- ✅ Menu utilisateur (paramètres, déconnexion)
- ✅ Responsive avec BreakpointObserver
- ✅ Routing actif avec highlight

---

## ✅ Routes avec Lazy Loading

### Routes principales
**Fichier:** `src/app/app.routes.ts`

```typescript
routes = [
  { path: '', redirectTo: '/dashboard' },
  { path: 'dashboard', loadComponent: ... },
  { path: 'clients', loadChildren: ... },
  { path: 'quotes', loadChildren: ... },
  { path: 'invoices', loadChildren: ... },
  { path: 'auth', loadChildren: ... }
]
```

### Routes par feature

#### Clients (`/clients`)
- `/clients` → Liste des clients
- `/clients/new` → Nouveau client
- `/clients/:id` → Détail client
- `/clients/:id/edit` → Édition client

#### Quotes (`/quotes`)
- `/quotes` → Liste des devis
- `/quotes/new` → Nouveau devis
- `/quotes/:id` → Détail devis
- `/quotes/:id/edit` → Édition devis

#### Invoices (`/invoices`)
- `/invoices` → Liste des factures
- `/invoices/new` → Nouvelle facture
- `/invoices/:id` → Détail facture
- `/invoices/:id/edit` → Édition facture

#### Auth (`/auth`)
- `/auth/login` → Connexion
- `/auth/register` → Inscription

---

## ✅ Composants Standalone

Tous les composants sont **standalone** (Angular 17+):
- Pas de NgModules
- Imports directs dans chaque composant
- Lazy loading avec `loadComponent()` et `loadChildren()`

---

## ✅ Prochaines étapes

### Core
1. **Guards** (`core/guards/`)
   - `auth.guard.ts` - Protection routes authentifiées
   - `role.guard.ts` - Protection par rôle (ADMIN, MANAGER)

2. **Interceptors** (`core/interceptors/`)
   - `auth.interceptor.ts` - Ajout token JWT aux requêtes
   - `error.interceptor.ts` - Gestion erreurs HTTP

3. **Services** (`core/services/`)
   - `auth.service.ts` - Service d'authentification
   - `api.service.ts` - Service API générique
   - `storage.service.ts` - Gestion localStorage/sessionStorage

### Features
1. **Auth Feature**
   - Implémenter `login.component.ts` avec formulaire
   - Implémenter `register.component.ts` avec formulaire

2. **Clients Feature**
   - Implémenter `clients-list.component.ts` avec table Material
   - Implémenter `client-form.component.ts` avec reactive forms
   - Implémenter `client-detail.component.ts`

3. **Quotes/Invoices Features**
   - Même structure que Clients

### Shared
1. **Components** (`shared/components/`)
   - `loading.component.ts` - Spinner de chargement
   - `confirm-dialog.component.ts` - Dialogue de confirmation
   - `error-message.component.ts` - Affichage erreurs

---

## ✅ Build Status

**Build réussi** ✓
- Tous les composants compilent
- Lazy loading configuré
- Routes fonctionnelles
- Material Design intégré

---

## 🚀 Démarrer l'application

```bash
cd frontend
npm start
```

L'application sera accessible sur `http://localhost:4200`

---

**Structure Angular 17+ complète et prête pour le développement !**

