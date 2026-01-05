# 🎨 GUIDE COMPLET - DESIGN SYSTEM MODERNE ERP-LITE

**Date:** 6 janvier 2026  
**Version:** 2.0.0  
**Type:** Design System & UI/UX Guidelines

---

## 📋 TABLE DES MATIÈRES

1. [Vue d'ensemble](#vue-densemble)
2. [Palette de couleurs](#palette-de-couleurs)
3. [Typographie](#typographie)
4. [Composants](#composants)
5. [Animations](#animations)
6. [Pages redesignées](#pages-redesignées)
7. [Guide d'implémentation](#guide-dimplémentation)

---

## 🎯 VUE D'ENSEMBLE

### **Philosophie du design**

Le nouveau design ERP-LITE est basé sur 5 principes fondamentaux :

1. **🌈 Coloré et vivant** - Utilisation de couleurs vives et harmonieuses pour chaque module
2. **✨ Moderne et épuré** - Design minimaliste avec des espaces aérés
3. **🎭 Interactif et animé** - Transitions fluides et micro-interactions
4. **📱 Responsive** - Adapté mobile, tablette et desktop
5. **🎯 Intuitif** - Navigation claire et actions évidentes

### **Amélioration par rapport à l'ancien design**

| Aspect | Ancien | Nouveau |
|--------|--------|---------|
| **Couleurs** | Gris/Violet sobre | Palette vive et harmonieuse |
| **Cartes** | Plates, ombres légères | Ombres prononcées, hover effects |
| **Boutons** | Standards Material | Arrondis, gradients, animations |
| **Badges** | Simples | Colorés avec icônes |
| **Tables** | Classiques | Modernes avec avatars et badges |
| **Animations** | Aucune | Fade-in, scale-in, hover effects |
| **Vues** | Liste uniquement | Grille + Liste (toggle) |

---

## 🎨 PALETTE DE COULEURS

### **Couleurs principales**

```scss
// Indigo vibrant (Principal)
$color-primary: #4F46E5
$color-primary-light: #818CF8
$color-primary-dark: #3730A3
$color-primary-bg: #EEF2FF

// Rose vibrant (Secondaire)
$color-secondary: #EC4899
$color-secondary-light: #F9A8D4
$color-secondary-dark: #BE185D
$color-secondary-bg: #FCE7F3
```

### **Couleurs fonctionnelles**

```scss
// Succès (Vert émeraude)
$color-success: #10B981
$color-success-bg: #D1FAE5

// Avertissement (Orange doré)
$color-warning: #F59E0B
$color-warning-bg: #FEF3C7

// Erreur (Rouge corail)
$color-error: #EF4444
$color-error-bg: #FEE2E2

// Info (Bleu ciel)
$color-info: #3B82F6
$color-info-bg: #DBEAFE
```

### **Couleurs métier**

Chaque module a sa propre couleur pour une identification visuelle rapide :

| Module | Couleur | Code | Usage |
|--------|---------|------|-------|
| **Devis** | Violet | `#8B5CF6` | Icônes, badges, accents |
| **Factures** | Cyan | `#06B6D4` | Icônes, badges, accents |
| **Clients** | Orange | `#F97316` | Icônes, badges, accents |
| **Catalogue** | Teal | `#14B8A6` | Icônes, badges, accents |

### **Neutrals (Gris modernes)**

```scss
$color-gray-50: #F9FAFB   // Background principal
$color-gray-100: #F3F4F6  // Background secondaire
$color-gray-200: #E5E7EB  // Bordures
$color-gray-500: #6B7280  // Texte secondaire
$color-gray-900: #111827  // Texte principal
```

---

## ✍️ TYPOGRAPHIE

### **Police**

```scss
$font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
```

### **Échelle de taille**

| Nom | Taille | Usage |
|-----|--------|-------|
| `font-size-xs` | 12px | Badges, hints |
| `font-size-sm` | 14px | Texte secondaire |
| `font-size-base` | 16px | Texte principal |
| `font-size-lg` | 18px | Sous-titres |
| `font-size-xl` | 20px | Titres de cartes |
| `font-size-2xl` | 24px | KPI values |
| `font-size-3xl` | 30px | Titres de page |

### **Poids**

```scss
$font-weight-normal: 400    // Texte normal
$font-weight-medium: 500    // Texte important
$font-weight-semibold: 600  // Titres
$font-weight-bold: 700      // Titres principaux
$font-weight-extrabold: 800 // KPI, highlights
```

---

## 🧩 COMPOSANTS

### **1. KPI Cards**

**Design :**
- Carte blanche avec ombre douce
- Bordure gauche colorée (4px)
- Icône avec gradient dans un cercle
- Valeur en gros (24px, bold)
- Label en petit (14px, medium)
- Effet de fond circulaire en transparence

**Code HTML :**
```html
<div class="kpi-card kpi-primary animate-fade-in">
  <div class="kpi-icon">
    <mat-icon>description</mat-icon>
  </div>
  <div class="kpi-content">
    <div class="kpi-value">{{ value }}</div>
    <div class="kpi-label">Label</div>
  </div>
</div>
```

**Variantes :**
- `kpi-primary` - Bleu indigo
- `kpi-success` - Vert
- `kpi-warning` - Orange
- `kpi-info` - Bleu ciel

---

### **2. Barre de recherche et filtres**

**Design :**
- Carte blanche avec ombre
- Input de recherche avec icône à gauche
- Boutons de filtre avec badges de comptage
- Toggle vue grille/liste à droite

**Features :**
- Recherche en temps réel
- Filtres actifs en couleur
- Badges avec nombre d'éléments
- Transitions douces

---

### **3. Vue Grille (Cards)**

**Design :**
- Grille responsive (auto-fill, minmax(340px, 1fr))
- Cartes avec hover effect (translateY(-2px))
- Bordure supérieure colorée au hover
- Avatar client avec gradient
- Badges de statut colorés
- Menu d'actions en bas à droite

**Éléments :**
- **Header** : Numéro + Badge statut
- **Client** : Avatar + Nom + Email
- **Détails** : Date + Validité (avec icônes)
- **Footer** : Montant + Menu actions

---

### **4. Vue Liste (Table moderne)**

**Design :**
- Table sans bordures verticales
- Header avec background gris clair
- Lignes avec hover effect
- Avatars dans les cellules
- Badges de statut inline
- Actions groupées à droite

**Features :**
- Cliquable pour voir le détail
- Actions rapides (voir, modifier, télécharger)
- Menu "plus d'actions" avec dividers

---

### **5. Badges de statut**

**Design :**
- Arrondis complets (border-radius: 9999px)
- Padding: 4px 12px
- Font-size: 12px, font-weight: 500
- Background coloré + texte assorti

**Variantes :**

| Statut | Background | Texte | Usage |
|--------|------------|-------|-------|
| Brouillon | `#E5E7EB` | `#374151` | DRAFT |
| Envoyé | `#FEF3C7` | `#F59E0B` | SENT |
| Signé | `#D1FAE5` | `#10B981` | SIGNED |
| Refusé | `#FEE2E2` | `#EF4444` | REJECTED |

---

### **6. Boutons modernes**

**Design :**
- Padding: 12px 20px
- Border-radius: 12px
- Font-weight: 500
- Box-shadow légère
- Hover: translateY(-1px) + ombre plus forte
- Active: translateY(0)

**Variantes :**
```scss
// Primaire
background: $color-primary
color: white
box-shadow: $shadow-primary

// Secondaire
background: white
color: $color-primary
border: 1px solid $border-light

// Danger
background: $color-error
color: white
```

---

### **7. Inputs modernes**

**Design :**
- Padding: 12px 16px
- Border-radius: 12px
- Border: 1px solid $border-light
- Focus: border-color: $color-primary + box-shadow bleu
- Placeholder: color: $text-hint

**Avec icône :**
```html
<div class="search-box">
  <mat-icon class="search-icon">search</mat-icon>
  <input class="search-input" placeholder="Rechercher..." />
</div>
```

---

## ✨ ANIMATIONS

### **1. Fade In (Entrée de page)**

```scss
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in {
  animation: fadeIn 300ms ease-out;
}
```

**Usage :** Header, KPI cards (avec delay progressif)

---

### **2. Scale In (Cartes)**

```scss
@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.animate-scale-in {
  animation: scaleIn 200ms ease-out;
}
```

**Usage :** Cartes de la grille

---

### **3. Hover Effects**

```scss
// Cartes
.card {
  transition: all 200ms cubic-bezier(0.4, 0, 0.2, 1);
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: $shadow-md;
  }
}

// Boutons
.button {
  transition: all 200ms cubic-bezier(0.4, 0, 0.2, 1);
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: $shadow-md;
  }
  
  &:active {
    transform: translateY(0);
  }
}
```

---

### **4. Loading Shimmer**

```scss
@keyframes shimmer {
  0% {
    background-position: -1000px 0;
  }
  100% {
    background-position: 1000px 0;
  }
}

.loading-shimmer {
  background: linear-gradient(
    90deg,
    $color-gray-100 0%,
    $color-gray-200 50%,
    $color-gray-100 100%
  );
  background-size: 1000px 100%;
  animation: shimmer 2s infinite;
}
```

**Usage :** Skeleton loaders

---

## 📄 PAGES REDESIGNÉES

### **✅ 1. PAGE DEVIS (Complète)**

**Fichiers modifiés :**
- `quotes-list.component.html` - Template moderne
- `quotes-list.component.scss` - Styles complets
- `quotes-list.component.ts` - Logique étendue

**Features implémentées :**
- ✅ Header avec gradient et icône badge
- ✅ 4 KPI cards animées
- ✅ Barre de recherche + filtres par statut
- ✅ Toggle vue grille/liste
- ✅ Vue grille avec cartes modernes
- ✅ Vue liste avec table moderne
- ✅ Animations fade-in et scale-in
- ✅ Hover effects sur toutes les cartes
- ✅ Menu d'actions complet
- ✅ États vides et loading

**Captures d'écran conceptuelles :**

```
┌─────────────────────────────────────────────────────────────┐
│ 📝 Devis                                                     │
│ Gérez vos devis et propositions commerciales                │
│                                              [+ Nouveau devis]│
├─────────────────────────────────────────────────────────────┤
│ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐                        │
│ │  📝  │ │  ✅  │ │  ⏰  │ │  💶  │                        │
│ │  42  │ │  12  │ │  18  │ │ 125K │                        │
│ │Total │ │Signés│ │Attent│ │Total │                        │
│ └──────┘ └──────┘ └──────┘ └──────┘                        │
├─────────────────────────────────────────────────────────────┤
│ 🔍 [Rechercher...]  [Tous 42] [Brouillon 12] [Envoyés 18]  │
│                                              [⊞] [≡]         │
├─────────────────────────────────────────────────────────────┤
│ ┌───────────┐ ┌───────────┐ ┌───────────┐                  │
│ │ DEV-001   │ │ DEV-002   │ │ DEV-003   │                  │
│ │ [Envoyé]  │ │ [Signé]   │ │ [Brouillon│                  │
│ │           │ │           │ │           │                  │
│ │ 👤 Client │ │ 👤 Client │ │ 👤 Client │                  │
│ │ Name      │ │ Name      │ │ Name      │                  │
│ │           │ │           │ │           │                  │
│ │ 📅 Date   │ │ 📅 Date   │ │ 📅 Date   │                  │
│ │ ⏰ 30j    │ │ ⏰ 30j    │ │ ⏰ 30j    │                  │
│ │           │ │           │ │           │                  │
│ │ 1 500€ ⋮  │ │ 2 800€ ⋮  │ │ 950€  ⋮   │                  │
│ └───────────┘ └───────────┘ └───────────┘                  │
└─────────────────────────────────────────────────────────────┘
```

---

### **⏳ 2. PAGE CLIENTS (À implémenter)**

**Design prévu :**
- Header orange avec icône "person"
- KPI : Total clients, Actifs, Inactifs, CA total
- Filtres : Tous, Particuliers, Professionnels
- Vue grille : Cartes avec avatar, nom, email, téléphone, CA
- Vue liste : Table avec colonnes optimisées
- Actions : Voir, Modifier, Historique, Supprimer

**Couleur principale :** `#F97316` (Orange)

---

### **⏳ 3. PAGE FACTURES (À implémenter)**

**Design prévu :**
- Header cyan avec icône "receipt"
- KPI : Total factures, Payées, En retard, Montant impayé
- Filtres : Toutes, Brouillon, Envoyées, Payées, En retard
- Vue grille : Cartes avec statut de paiement proéminent
- Vue liste : Table avec indicateur de retard
- Actions : Voir, Modifier, Télécharger, Relancer, Marquer payée

**Couleur principale :** `#06B6D4` (Cyan)

---

### **⏳ 4. PAGE MES TARIFS (À implémenter)**

**Design prévu :**
- Header teal avec icône "inventory_2"
- KPI : Total articles, Actifs, Stock bas, Valeur totale
- Filtres : Tous, Par catégorie (Robinetterie, Chauffage, etc.)
- Vue grille : Cartes avec image, prix, stock
- Vue liste : Table avec indicateur de stock
- Actions : Voir, Modifier, Dupliquer, Activer/Désactiver, Supprimer

**Couleur principale :** `#14B8A6` (Teal)

---

## 🛠️ GUIDE D'IMPLÉMENTATION

### **Étape 1 : Importer le Design System**

Dans votre composant SCSS :

```scss
@import '../../../../styles/design-system';
```

### **Étape 2 : Utiliser les mixins**

```scss
// Card moderne
.my-card {
  @include card-modern;
}

// Bouton moderne
.my-button {
  @include button-modern($text-white, $color-primary);
}

// Badge moderne
.my-badge {
  @include badge-modern($color-success-bg, $color-success);
}

// Input moderne
.my-input {
  @include input-modern;
}
```

### **Étape 3 : Ajouter les animations**

```html
<!-- Fade in avec delay -->
<div class="kpi-card animate-fade-in" style="animation-delay: 0.1s">
  ...
</div>

<!-- Scale in -->
<div class="quote-card animate-scale-in">
  ...
</div>
```

### **Étape 4 : Utiliser les couleurs**

```scss
// Couleurs métier
.quote-badge {
  background: $color-quote;
  color: white;
}

.invoice-badge {
  background: $color-invoice;
  color: white;
}

// Couleurs fonctionnelles
.success-badge {
  background: $color-success-bg;
  color: $color-success;
}
```

---

## 📐 RESPONSIVE DESIGN

### **Breakpoints**

```scss
$breakpoint-sm: 640px   // Mobile large
$breakpoint-md: 768px   // Tablette
$breakpoint-lg: 1024px  // Desktop
$breakpoint-xl: 1280px  // Large desktop
```

### **Mixin responsive**

```scss
@include responsive(md) {
  // Styles pour tablette et plus
}
```

### **Adaptations mobiles**

```scss
// Grille → 1 colonne
.quotes-grid {
  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

// Header → Stack vertical
.page-header .header-content {
  @media (max-width: $breakpoint-md) {
    flex-direction: column;
    align-items: flex-start;
  }
}

// Cacher certains éléments
.desktop-only {
  @media (max-width: $breakpoint-md) {
    display: none;
  }
}
```

---

## 🎯 CHECKLIST D'IMPLÉMENTATION

### **Pour chaque page :**

- [ ] Créer le header avec icône badge colorée
- [ ] Ajouter 4 KPI cards avec animations
- [ ] Implémenter la barre de recherche
- [ ] Ajouter les filtres par statut/catégorie
- [ ] Créer le toggle vue grille/liste
- [ ] Designer la vue grille avec cartes modernes
- [ ] Designer la vue liste avec table moderne
- [ ] Ajouter les animations (fade-in, scale-in, hover)
- [ ] Implémenter les états vides et loading
- [ ] Ajouter le menu d'actions complet
- [ ] Tester le responsive (mobile, tablette, desktop)
- [ ] Vérifier l'accessibilité (contraste, focus, aria-labels)

---

## 🚀 PROCHAINES ÉTAPES

1. **Implémenter les 3 pages restantes** (Clients, Factures, Catalogue)
2. **Ajouter des micro-interactions** (ripple effects, success animations)
3. **Créer des composants réutilisables** (KPI card, search bar, status badge)
4. **Optimiser les performances** (lazy loading, virtual scrolling)
5. **Ajouter des graphiques** (charts.js ou apex charts)
6. **Tests utilisateurs** et ajustements

---

**🎨 Design System créé avec ❤️ pour ERP-LITE**

*Dernière mise à jour : 6 janvier 2026, 18:30*

