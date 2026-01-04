# Guide d'Internationalisation (i18n) - ERP-LITE

## 📦 Configuration Complète

Toute l'application a été configurée pour l'internationalisation avec **ngx-translate** et un **pipe capitalize** personnalisé.

## 🎯 Ce qui a été fait

### 1. Packages Installés
```bash
npm install @ngx-translate/core @ngx-translate/http-loader
```

### 2. Structure Créée

```
frontend/src/
├── app/
│   ├── shared/
│   │   └── pipes/
│   │       ├── capitalize.pipe.ts    # Pipe pour capitaliser la première lettre
│   │       └── index.ts              # Export des pipes
│   ├── core/
│   │   └── utils/
│   │       └── translation-loader.ts # Loader pour charger les fichiers JSON
│   └── app.config.ts                 # Configuration ngx-translate
└── assets/
    └── i18n/
        └── fr.json                   # Fichier de traductions françaises (98 clés)
```

### 3. Fichiers Transformés (13 fichiers HTML)

**Tous les textes** ont été remplacés par le format :
```html
{{ 'cle_traduction' | translate | capitalize }}
```

**Exemple :**
```html
<!-- Avant -->
<span>Déconnexion</span>

<!-- Après -->
<span>{{ 'deconnexion' | translate | capitalize }}</span>
```

### 4. Configuration

#### `app.config.ts`
- Import de `TranslateModule` avec configuration
- Langue par défaut : **français (fr)**
- Loader configuré pour charger `/assets/i18n/{lang}.json`

#### `app.component.ts`
- Import de `TranslateModule` et `CapitalizePipe`
- Initialisation du service de traduction dans le constructeur
- Labels du menu convertis en clés i18n

## 📝 Fichier de Traductions

Le fichier `src/assets/i18n/fr.json` contient **98 clés de traduction** organisées par catégories :

### Navigation
```json
{
  "dashboard": "Dashboard",
  "clients": "Clients",
  "devis": "Devis",
  "factures": "Factures",
  "parametres": "Paramètres",
  "deconnexion": "Déconnexion"
}
```

### Authentification
```json
{
  "connexion": "Connexion",
  "inscription": "Inscription",
  "email": "Email",
  "mot de passe": "Mot de passe",
  "se connecter": "Se connecter",
  "s inscrire": "S'inscrire"
}
```

### Formulaires
```json
{
  "prenom": "Prénom",
  "nom": "Nom",
  "telephone": "Téléphone",
  "adresse": "Adresse",
  "ville": "Ville",
  "code postal": "Code postal"
}
```

### Actions
```json
{
  "voir": "Voir",
  "modifier": "Modifier",
  "supprimer": "Supprimer",
  "annuler": "Annuler",
  "enregistrer": "Enregistrer",
  "creer": "Créer"
}
```

## 🚀 Comment Utiliser

### Dans les Templates HTML

**Texte simple :**
```html
<h1>{{ 'tableau de bord' | translate | capitalize }}</h1>
```

**Dans les attributs :**
```html
<mat-label>{{ 'email' | translate | capitalize }}</mat-label>
<input matInput placeholder="{{ 'nom email siret' | translate | capitalize }}">
```

**Dans les boutons :**
```html
<button mat-raised-button>
  {{ 'nouveau client' | translate | capitalize }}
</button>
```

**Expressions ternaires :**
```html
{{ isEditMode ? ('modifier le client' | translate | capitalize) : ('nouveau client' | translate | capitalize) }}
```

### Dans les Composants TypeScript

Si vous avez besoin de traductions dans le code TypeScript :

```typescript
import { TranslateService } from '@ngx-translate/core';

constructor(private translate: TranslateService) {}

// Obtenir une traduction
const message = this.translate.instant('bienvenue');

// Avec interpolation
const messageWithParam = this.translate.instant('bienvenue', { name: 'Jean' });
```

## 🌍 Ajouter une Nouvelle Langue

### 1. Créer le fichier de traduction

Créez un nouveau fichier dans `src/assets/i18n/` :
```bash
src/assets/i18n/en.json  # Pour l'anglais
src/assets/i18n/es.json  # Pour l'espagnol
```

### 2. Copier et traduire

Copiez le contenu de `fr.json` et traduisez les valeurs :

```json
{
  "dashboard": "Dashboard",
  "clients": "Clients",
  "devis": "Quotes",
  "factures": "Invoices",
  "parametres": "Settings",
  "deconnexion": "Logout"
}
```

### 3. Changer de langue

Dans le code TypeScript :
```typescript
this.translate.use('en');  // Basculer vers l'anglais
this.translate.use('fr');  // Retour au français
```

## 🔧 Le Pipe Capitalize

Le pipe `capitalize` convertit automatiquement la première lettre en majuscule :

```typescript
// Pipe: src/app/shared/pipes/capitalize.pipe.ts

'bonjour' | capitalize  → 'Bonjour'
'tableau de bord' | capitalize  → 'Tableau de bord'
```

## ✅ Avantages

1. **Toutes les traductions centralisées** dans `fr.json`
2. **Facile d'ajouter de nouvelles langues** (juste créer un nouveau fichier JSON)
3. **Code propre** avec des clés explicites
4. **Type-safe** avec TypeScript
5. **Performance optimisée** avec le chargement à la demande

## 📚 Ressources

- [Documentation ngx-translate](https://github.com/ngx-translate/core)
- [Guide Angular i18n](https://angular.io/guide/i18n)

## 🎉 Résultat

Votre application ERP-LITE est maintenant **100% prête pour l'internationalisation** !

Tous les textes sont traduits via le système i18n, et vous pouvez facilement ajouter de nouvelles langues en créant de nouveaux fichiers JSON.
