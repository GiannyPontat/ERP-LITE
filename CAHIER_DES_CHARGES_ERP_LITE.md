# CAHIER DES CHARGES - ERP-LITE
## Application de Gestion pour Professionnels du Bâtiment

**Version:** 1.0  
**Date:** 02/01/2026  
**Stack Technique:** Angular + Spring Boot + PostgreSQL/MySQL

---

## 📋 CONTEXTE DU PROJET

### Objectif
Créer une application web de gestion complète pour les artisans et PME du secteur du bâtiment, inspirée de **Costructor**, avec toutes ses fonctionnalités principales.

### Public cible
- Micro-entrepreneurs du BTP
- Artisans (plombiers, électriciens, maçons, etc.)
- TPE et PME du bâtiment
- Entreprises de construction

---

## 🎯 FONCTIONNALITÉS PRINCIPALES

### 1. GESTION DE LA FACTURATION

#### 1.1 Création de devis
- **Éditeur de devis personnalisable**
  - Ajout du logo de l'entreprise
  - Modification des polices et couleurs
  - Types de tableaux personnalisables
  - Numérotation automatique
  - Conditions générales modifiables

- **Bibliothèque de prix BTP**
  - Base de données de 26 000+ références (type Batiprix)
  - Recherche par mot-clé
  - Catégorisation par corps de métier
  - Mise à jour régulière des tarifs

- **Fonctionnalités avancées**
  - Conversion devis → facture en 1 clic
  - Duplication de devis existants
  - Versions multiples d'un même devis
  - Historique des modifications
  - Export PDF

#### 1.2 Création de factures
- **Types de factures**
  - Factures standards
  - Factures d'acompte
  - Factures d'avoirs (avoir)
  - Factures de situation

- **Gestion des factures**
  - Génération d'attestations de TVA
  - Calcul automatique de TVA
  - Application de remises
  - Ajout de pénalités de retard
  - Mentions légales obligatoires

#### 1.3 Envoi et relances
- **Envoi par email**
  - Template d'email personnalisable
  - Envoi automatique ou manuel
  - Confirmation de lecture
  - Pièces jointes multiples

- **Système de relances**
  - Relances automatiques programmables
  - Modèles de courriers de relance
  - Suivi des relances envoyées
  - Escalade des relances (rappel → mise en demeure)

---

### 2. GESTION DES CLIENTS ET FOURNISSEURS

#### 2.1 Fiches clients
- **Informations de base**
  - Raison sociale
  - Forme juridique (particulier, entreprise)
  - SIRET / SIREN
  - Adresse de facturation
  - Adresse de chantier (si différente)
  - Coordonnées (téléphone, email)
  - Contact principal

- **Données comptables**
  - Conditions de paiement
  - Mode de paiement préféré
  - Historique des transactions
  - Encours client
  - Crédit autorisé

- **Historique relationnel**
  - Devis envoyés
  - Factures émises
  - Paiements reçus
  - Notes et commentaires
  - Documents attachés

#### 2.2 Fiches fournisseurs
- Coordonnées complètes
- Catalogues de produits
- Conditions tarifaires
- Délais de livraison
- Historique des commandes

#### 2.3 Portail client
- **Accès sécurisé pour les clients**
  - Authentification par email + mot de passe
  - Consultation des devis
  - Consultation des factures
  - Téléchargement des documents
  - Signature électronique des devis
  - Historique complet des échanges

---

### 3. GESTION DES PAIEMENTS

#### 3.1 Moyens de paiement
- **Intégration Stripe (ou équivalent)**
  - Carte bancaire (CB, Visa, Mastercard)
  - Apple Pay
  - Google Pay
  - Virement SEPA
  - Prélèvement SEPA

#### 3.2 Suivi des paiements
- Statut des factures (payée, impayée, partielle)
- Rapprochement bancaire
- Lettrage automatique
- Gestion des acomptes
- Relances automatiques pour impayés

---

### 4. GESTION DES CHANTIERS

#### 4.1 Création et suivi de chantiers
- **Informations du chantier**
  - Nom du projet
  - Client associé
  - Adresse du chantier
  - Date de début / Date de fin prévisionnelle
  - Budget prévisionnel
  - Dépenses réelles
  - Marge prévisionnelle / réelle

#### 4.2 Planification
- **Diagramme de Gantt**
  - Création de tâches
  - Dépendances entre tâches
  - Affectation de ressources (employés, sous-traitants)
  - Suivi de l'avancement
  - Alertes pour retards
  - Vue calendrier
  - Vue liste de tâches

#### 4.3 Documents de chantier
- **Stockage centralisé**
  - Plans et schémas
  - Photos (avant/pendant/après)
  - Devis liés au chantier
  - Factures fournisseurs
  - Bons de livraison
  - PV de réception
  - Documents techniques
  - Classement par dossiers

- **Partage de documents**
  - Partage avec le client (portail)
  - Partage avec les équipes
  - Droits d'accès configurables
  - Commentaires sur documents

---

### 5. GESTION DES STOCKS ET COMMANDES

#### 5.1 Gestion des stocks
- **Inventaire**
  - Liste des articles en stock
  - Quantités disponibles
  - Seuils d'alerte
  - Valeur du stock
  - Mouvements de stock (entrées/sorties)

- **Catégorisation**
  - Par type de matériel
  - Par emplacement
  - Par fournisseur

#### 5.2 Bons de commande
- Création de bons de commande fournisseur
- Réception de marchandises
- Mise à jour automatique du stock
- Rapprochement commande/livraison/facture

---

### 6. PILOTAGE ET TABLEAUX DE BORD

#### 6.1 Dashboard principal
- **Vue d'ensemble**
  - Chiffre d'affaires du mois
  - Bénéfices
  - Factures en attente de paiement
  - Devis en cours
  - Chantiers actifs
  - Trésorerie prévisionnelle

- **Graphiques et indicateurs**
  - Évolution du CA (mensuel, annuel)
  - Répartition par type de prestation
  - Top 10 clients
  - Taux de transformation devis → factures
  - Délai moyen de paiement

#### 6.2 Suivi de rentabilité
- **Par chantier**
  - Budget vs Réel
  - Marge brute / nette
  - Taux de rentabilité
  - Écarts budgétaires

- **Global**
  - Rentabilité prévisionnelle
  - Rentabilité réelle
  - Dépenses par catégorie
  - Charges fixes vs variables

#### 6.3 Synchronisation bancaire
- Connexion sécurisée au compte bancaire
- Import automatique des transactions
- Rapprochement avec les factures
- Catégorisation des dépenses
- Situation de trésorerie en temps réel

#### 6.4 Lecture automatique de factures (OCR)
- Scan ou upload de factures fournisseurs
- Extraction automatique des données
- Validation et correction manuelle
- Enregistrement automatique en comptabilité

---

### 7. GESTION DES UTILISATEURS ET RÔLES

#### 7.1 Système de rôles
- **ADMIN**
  - Accès complet à toutes les fonctionnalités
  - Gestion des utilisateurs
  - Configuration de l'entreprise
  - Accès aux données financières

- **MANAGER**
  - Gestion des chantiers
  - Création de devis et factures
  - Suivi des paiements
  - Consultation des tableaux de bord
  - Pas d'accès aux paramètres entreprise

- **EMPLOYEE** (Employé/Ouvrier)
  - Consultation des chantiers assignés
  - Ajout de notes et photos sur chantier
  - Consultation des documents
  - Pas d'accès aux données financières

#### 7.2 Multi-utilisateurs
- Gestion des accès et permissions
- Logs d'activité par utilisateur
- Attribution de chantiers par utilisateur

---

### 8. PARAMÉTRAGE ENTREPRISE

#### 8.1 Informations de l'entreprise
- Raison sociale
- SIRET / SIREN
- Forme juridique
- Logo
- Coordonnées (adresse, téléphone, email)
- RCS / RM
- Numéro de TVA intracommunautaire
- Assurances (RC Pro, décennale)

#### 8.2 Personnalisation des documents
- Templates de devis
- Templates de factures
- Templates d'emails
- Mentions légales
- Conditions générales de vente
- Pied de page personnalisé

#### 8.3 Paramètres de facturation
- Numérotation automatique
- Taux de TVA par défaut
- Conditions de paiement standards
- Pénalités de retard
- Escompte pour paiement anticipé

---

### 9. CONFORMITÉ ET SÉCURITÉ

#### 9.1 Conformité légale
- **Loi anti-fraude à la TVA**
  - Conformité avec la réglementation française
  - Certification NF525 (si applicable)
  - Archivage légal des documents

- **RGPD**
  - Consentement des clients
  - Droit à l'oubli
  - Export des données personnelles
  - Politique de confidentialité

#### 9.2 Sécurité
- **Hébergement**
  - Serveurs en France (ou UE)
  - Sauvegarde quotidienne
  - Chiffrement des données sensibles
  - SSL/TLS obligatoire

- **Authentification**
  - Mot de passe sécurisé
  - Authentification à deux facteurs (2FA) optionnelle
  - Déconnexion automatique après inactivité
  - Gestion des sessions

---

### 10. ACCESSIBILITÉ ET COMPATIBILITÉ

#### 10.1 Application web
- Responsive design (desktop, tablette, mobile)
- Compatible tous navigateurs modernes (Chrome, Firefox, Safari, Edge)
- Progressive Web App (PWA)
  - Installation sur écran d'accueil mobile
  - Mode hors ligne (consultation uniquement)
  - Notifications push

#### 10.2 Performances
- Temps de chargement < 2 secondes
- Support de plusieurs milliers de documents
- Optimisation des requêtes base de données

---

## 🛠️ ARCHITECTURE TECHNIQUE

### Stack Frontend
- **Angular 17+**
  - TypeScript
  - Angular Material UI / PrimeNG
  - RxJS pour la gestion d'état
  - PWA support

### Stack Backend
- **Spring Boot 3.x**
  - Java 17+
  - Spring Security (authentification JWT)
  - Spring Data JPA
  - Spring Validation
  - API RESTful

### Base de données
- **PostgreSQL** (recommandé) ou **MySQL**
  - Flyway pour les migrations
  - Index optimisés
  - Contraintes d'intégrité

### Services tiers
- **Paiements:** Stripe API
- **Email:** SendGrid / Mailgun / Amazon SES
- **Stockage:** AWS S3 / Google Cloud Storage (pour documents)
- **OCR:** Google Cloud Vision / AWS Textract
- **Connexion bancaire:** Bridge API / Budget Insight

---

## 📦 LIVRABLES

### Phase 1 : MVP (3-4 mois)
- Gestion des clients
- Création devis et factures basique
- Système de rôles (Admin, Manager, Employee)
- Tableau de bord simple
- Authentification

### Phase 2 : Fonctionnalités métier (2-3 mois)
- Bibliothèque de prix BTP
- Gestion des chantiers + Gantt
- Portail client
- Envoi emails et relances
- Stockage de documents

### Phase 3 : Automatisation (2 mois)
- Intégration Stripe
- Synchronisation bancaire
- OCR factures
- Rapports avancés

### Phase 4 : Optimisation (1 mois)
- PWA
- Optimisations performances
- Tests utilisateurs
- Corrections bugs

---

## 📊 INDICATEURS DE SUCCÈS

### KPIs techniques
- Temps de réponse API < 200ms
- Disponibilité > 99.5%
- Zéro perte de données

### KPIs métier
- Gain de temps : 50% sur la facturation
- Taux d'adoption : 80% des fonctionnalités utilisées
- Satisfaction utilisateur : > 4/5

---

## 🚀 PRIORISATION DES FONCTIONNALITÉS

### 🔴 CRITIQUE (MVP)
1. Authentification et gestion des utilisateurs
2. Création de devis et factures
3. Gestion des clients
4. Export PDF
5. Envoi par email

### 🟠 IMPORTANT (Phase 2)
6. Bibliothèque de prix
7. Gestion des chantiers
8. Tableau de bord
9. Portail client
10. Stockage de documents

### 🟡 SOUHAITABLE (Phase 3)
11. Intégration paiements
12. Relances automatiques
13. Synchronisation bancaire
14. OCR factures
15. Diagramme de Gantt

### 🟢 BONUS (Phase 4)
16. PWA
17. Mode hors ligne
18. Application mobile native
19. Intégrations comptables (Sage, Ciel)
20. API publique

---

## 💰 MODÈLE ÉCONOMIQUE (Optionnel)

### Freemium
- **Gratuit**
  - 5 devis/factures par mois
  - 1 utilisateur
  - Bibliothèque de base

- **Pro** (20-30€/mois)
  - Devis/factures illimités
  - 3 utilisateurs
  - Bibliothèque complète
  - Support prioritaire

- **Business** (50-80€/mois)
  - Utilisateurs illimités
  - Intégrations avancées
  - Synchronisation bancaire
  - OCR factures

- **Business+** (100-150€/mois)
  - Toutes fonctionnalités
  - API access
  - Support dédié
  - Formation personnalisée

---

## 📝 NOTES COMPLÉMENTAIRES

### Design UI/UX
- Interface moderne et épurée
- Navigation intuitive (max 3 clics pour toute action)
- Codes couleurs pour les statuts (vert = payé, orange = en attente, rouge = impayé)
- Animations fluides
- Mode sombre optionnel

### Accessibilité
- Conformité WCAG 2.1 niveau AA
- Support clavier complet
- Lecteurs d'écran compatibles

### Internationalisation
- Multi-langue (FR, EN en priorité)
- Multi-devises
- Formats de date/nombre localisés

---

**FIN DU CAHIER DES CHARGES**
