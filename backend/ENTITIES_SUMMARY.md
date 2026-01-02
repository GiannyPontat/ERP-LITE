# Résumé des Entités JPA - ERP-LITE

## ✅ Entités Créées/Modifiées

### 1. **User.java** ✓
**Localisation:** `models/User.java`

**Champs selon spécification:**
- `id` (Long) - Identifiant principal (gardé en Long pour compatibilité)
- `uuid` (UUID) - Identifiant UUID selon spec (auto-généré)
- `email` (String, unique, not null)
- `password` (String, not null)
- `firstName` (String)
- `lastName` (String)
- `role` (UserRole enum) - Nouveau champ selon spec
- `company` (String) - Nouveau champ selon spec
- `active` (Boolean) - Nouveau champ selon spec
- `createdAt` (LocalDateTime) - Nouveau champ selon spec
- `updatedAt` (LocalDateTime) - Nouveau champ selon spec

**Champs de compatibilité (existants):**
- `enabled` (Boolean) - Pour compatibilité avec code existant
- `emailVerified` (Boolean) - Pour compatibilité avec code existant
- `roles` (Set<Role>) - Relation ManyToMany pour compatibilité

**Annotations Lombok:** `@Data`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`
**Méthodes:** `dto()`, `generateUuid()` (PrePersist), `onUpdate()` (PreUpdate)

---

### 2. **Client.java** ✓
**Localisation:** `models/Client.java`

**Champs selon spécification:**
- `id` (Long) - Identifiant principal
- `companyName` (String) - Nom de l'entreprise
- `siret` (String) - Numéro SIRET
- `contactFirstName` (String) - Prénom du contact
- `contactLastName` (String) - Nom du contact
- `email` (String)
- `phone` (String)
- `address` (String)
- `city` (String)
- `postalCode` (String)
- `paymentTerms` (Integer) - Conditions de paiement en jours
- `notes` (String, TEXT)

**Relations:**
- `user` (ManyToOne) - Relation avec User selon spec
- `quotes` (OneToMany) - Liste des devis
- `invoices` (OneToMany) - Liste des factures

**Champs de compatibilité:**
- `nom`, `entreprise`, `telephone`, `adresse` - Pour compatibilité avec code existant
- `devis` (OneToMany) - Relation avec l'ancien système Devis

**Méthodes:** `dto()` - Méthode de compatibilité

---

### 3. **Quote.java** ✓
**Localisation:** `models/Quote.java`

**Champs selon spécification:**
- `id` (Long) - Identifiant principal
- `quoteNumber` (String, unique, not null) - Format "DEV-2026-0001" (auto-généré)
- `client` (ManyToOne) - Relation avec Client
- `createdBy` (ManyToOne) - Relation avec User (créateur)
- `date` (LocalDate, not null)
- `validUntil` (LocalDate)
- `status` (QuoteStatus enum) - DRAFT, SENT, ACCEPTED, REJECTED, EXPIRED, CONVERTED
- `subtotal` (BigDecimal, precision 19, scale 2)
- `taxRate` (BigDecimal, precision 5, scale 2)
- `taxAmount` (BigDecimal)
- `total` (BigDecimal)
- `notes` (String, TEXT)
- `termsAndConditions` (String, TEXT)
- `items` (OneToMany) - Liste des QuoteItem

**Méthodes:** `generateQuoteNumber()` (PrePersist)

---

### 4. **QuoteItem.java** ✓
**Localisation:** `models/QuoteItem.java`

**Champs selon spécification:**
- `id` (Long)
- `quote` (ManyToOne) - Relation avec Quote
- `description` (String, TEXT, not null)
- `quantity` (Integer, min 1, not null)
- `unitPrice` (BigDecimal, precision 19, scale 2)
- `total` (BigDecimal) - Calculé automatiquement (quantity × unitPrice)

**Validations:** `@NotBlank`, `@NotNull`, `@Min(1)`
**Méthodes:** `calculateTotal()` (PrePersist, PreUpdate)

---

### 5. **Invoice.java** ✓
**Localisation:** `models/Invoice.java`

**Champs selon spécification:**
- `id` (Long)
- `invoiceNumber` (String, unique, not null) - Format "FACT-2026-0001" (auto-généré)
- `client` (ManyToOne) - Relation avec Client
- `createdBy` (ManyToOne) - Relation avec User (créateur)
- `quote` (ManyToOne, optional) - Relation avec Quote si conversion depuis devis
- `date` (LocalDate, not null)
- `dueDate` (LocalDate) - Date d'échéance
- `paidDate` (LocalDate) - Date de paiement
- `status` (InvoiceStatus enum) - DRAFT, SENT, PAID, OVERDUE, CANCELLED, PARTIALLY_PAID
- `subtotal` (BigDecimal)
- `taxRate` (BigDecimal)
- `taxAmount` (BigDecimal)
- `total` (BigDecimal)
- `notes` (String, TEXT)
- `termsAndConditions` (String, TEXT)
- `items` (OneToMany) - Liste des InvoiceItem

**Méthodes:** `generateInvoiceNumber()` (PrePersist)

---

### 6. **InvoiceItem.java** ✓
**Localisation:** `models/InvoiceItem.java`

**Champs selon spécification:**
- `id` (Long)
- `invoice` (ManyToOne) - Relation avec Invoice
- `description` (String, TEXT, not null)
- `quantity` (Integer, min 1, not null)
- `unitPrice` (BigDecimal)
- `total` (BigDecimal) - Calculé automatiquement

**Validations:** `@NotBlank`, `@NotNull`, `@Min(1)`
**Méthodes:** `calculateTotal()` (PrePersist, PreUpdate)

---

## 📋 Enums Créés

### 1. **UserRole.java** ✓
```java
ADMIN, USER, MANAGER, ACCOUNTANT
```

### 2. **QuoteStatus.java** ✓
```java
DRAFT, SENT, ACCEPTED, REJECTED, EXPIRED, CONVERTED
```

### 3. **InvoiceStatus.java** ✓
```java
DRAFT, SENT, PAID, OVERDUE, CANCELLED, PARTIALLY_PAID
```

---

## 🔗 Relations entre Entités

```
User
 ├─→ Client (1-N) : Un utilisateur peut avoir plusieurs clients
 └─→ Quote (1-N) : Un utilisateur peut créer plusieurs devis
 └─→ Invoice (1-N) : Un utilisateur peut créer plusieurs factures

Client
 ├─→ Quote (1-N) : Un client peut avoir plusieurs devis
 ├─→ Invoice (1-N) : Un client peut avoir plusieurs factures
 └─→ User (N-1) : Plusieurs clients appartiennent à un utilisateur

Quote
 ├─→ Client (N-1) : Un devis appartient à un client
 ├─→ User (N-1) : Un devis est créé par un utilisateur
 ├─→ QuoteItem (1-N) : Un devis contient plusieurs lignes
 └─→ Invoice (1-N) : Un devis peut être converti en facture(s)

Invoice
 ├─→ Client (N-1) : Une facture appartient à un client
 ├─→ User (N-1) : Une facture est créée par un utilisateur
 ├─→ Quote (N-1) : Une facture peut provenir d'un devis (optional)
 └─→ InvoiceItem (1-N) : Une facture contient plusieurs lignes
```

---

## 🗄️ Tables à Créer (Migration Flyway)

Les nouvelles tables suivantes devront être ajoutées via une migration Flyway :

1. `gp_erp_quote` - Table pour les devis
2. `gp_erp_quote_item` - Table pour les lignes de devis
3. `gp_erp_invoice` - Table pour les factures
4. `gp_erp_invoice_item` - Table pour les lignes de factures

**Note:** Les tables existantes (`gp_erp_user`, `gp_erp_client`) ont été mises à jour avec de nouveaux champs.

---

## ⚙️ Fonctionnalités Implémentées

1. ✅ **Auto-génération des numéros**
   - `quoteNumber`: Format "DEV-YYYY-XXXX" (à implémenter dans le service)
   - `invoiceNumber`: Format "FACT-YYYY-XXXX" (à implémenter dans le service)

2. ✅ **Calcul automatique des totaux**
   - `QuoteItem.total` = quantity × unitPrice
   - `InvoiceItem.total` = quantity × unitPrice

3. ✅ **Validation Bean Validation**
   - Contraintes sur les champs obligatoires
   - Validation des quantités (min 1)
   - Validation des formats

4. ✅ **Compatibilité avec code existant**
   - User garde les champs `enabled`, `emailVerified`, `roles`
   - Client garde les champs `nom`, `entreprise`, `telephone`, `adresse`
   - Méthodes `dto()` pour compatibilité

---

## 📝 Notes Importantes

1. **UUID vs Long:** 
   - Les IDs principaux sont en `Long` pour compatibilité avec le code existant
   - `User` a un champ `uuid` supplémentaire selon la spec

2. **Numéros auto-générés:**
   - Les numéros de devis et factures sont générés avec un format temporaire dans `@PrePersist`
   - Une implémentation complète nécessitera un service avec séquence/compteur

3. **Relations:**
   - Toutes les relations utilisent `FetchType.LAZY` pour optimiser les performances
   - Les relations OneToMany utilisent `cascade = CascadeType.ALL` et `orphanRemoval = true`

4. **Compatibilité:**
   - Les entités gardent la compatibilité avec l'ancien système (Devis, LigneDevis)
   - Les nouvelles entités (Quote, Invoice) coexistent avec les anciennes

---

## ✅ Statut de Compilation

**BUILD SUCCESS** - Toutes les entités compilent correctement ✓

