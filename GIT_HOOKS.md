# 🪝 Git Hooks Configuration - ERP LITE

Ce projet utilise des **pre-commit hooks** pour garantir la qualité du code avant chaque commit.

## 📋 Hooks installés

### 1. **Pre-commit Hook** (`pre-commit`)

Vérifie automatiquement avant chaque commit :

✅ **Sécurité**
- Détecte les fichiers sensibles (`.env`, `*.key`, credentials)
- Recherche les secrets/passwords dans le code
- Limite la taille des fichiers (max 5 MB)

✅ **Backend Java**
- Compilation du code Java
- ~~Tests unitaires~~ (désactivé par défaut pour la rapidité)

✅ **Frontend Angular**
- ~~Linting ESLint~~ (désactivé par défaut)
- ~~Build check~~ (désactivé par défaut)

### 2. **Commit-msg Hook** (`commit-msg`)

Vérifie le message de commit :
- Longueur minimum : 10 caractères
- Longueur maximum recommandée : 72 caractères
- ~~Format Conventional Commits~~ (désactivé par défaut)

## 🎯 Conventional Commits (Optionnel)

Format recommandé : `type(scope): message`

**Types :**
- `feat`: Nouvelle fonctionnalité
- `fix`: Correction de bug
- `docs`: Documentation
- `style`: Formatage
- `refactor`: Refactoring
- `test`: Tests
- `chore`: Maintenance
- `perf`: Performance

**Exemples :**
```bash
git commit -m "feat(email): add quote email sending"
git commit -m "fix(pdf): correct invoice generation error"
git commit -m "docs: update email configuration guide"
```

## ⚙️ Personnalisation

### Activer les tests backend

Éditez `.git/hooks/pre-commit` et décommentez :
```bash
# Tests unitaires (optionnel - commentez si trop long)
echo "  - Exécution des tests..."
if ! ./mvnw test -q 2>&1 | grep -q "BUILD SUCCESS"; then
    echo -e "${RED}❌ Les tests ont échoué${NC}"
    cd ..
    exit 1
fi
```

### Activer le linting frontend

Éditez `.git/hooks/pre-commit` et décommentez :
```bash
# Linting
echo "  - Linting du code..."
if ! npm run lint --silent 2>&1 | grep -q "successfully"; then
    echo -e "${RED}❌ Le linting a échoué${NC}"
    cd ..
    exit 1
fi
```

### Activer Conventional Commits

Éditez `.git/hooks/commit-msg` et décommentez la section de validation.

## 🚫 Bypass des hooks (déconseillé)

En cas d'urgence, vous pouvez bypasser les hooks :

```bash
git commit --no-verify -m "message"
```

⚠️ **Attention :** À utiliser uniquement en cas d'urgence !

## 🔧 Désinstallation

Pour désactiver les hooks :

```bash
rm .git/hooks/pre-commit
rm .git/hooks/commit-msg
```

## 📊 Ce qui est vérifié

| Vérification | Pre-commit | Commit-msg | Activé |
|-------------|-----------|-----------|---------|
| Fichiers sensibles | ✅ | ❌ | ✅ |
| Secrets dans le code | ✅ | ❌ | ✅ |
| Taille des fichiers | ✅ | ❌ | ✅ |
| Compilation Java | ✅ | ❌ | ✅ |
| Tests Java | ✅ | ❌ | ❌ |
| Linting Angular | ✅ | ❌ | ❌ |
| Build Angular | ✅ | ❌ | ❌ |
| Longueur du message | ❌ | ✅ | ✅ |
| Format Conventional | ❌ | ✅ | ❌ |

## 🎨 Exemple de workflow

```bash
# 1. Modifier du code
vim backend/src/main/java/com/gp_dev/erp_lite/services/EmailService.java

# 2. Ajouter les modifications
git add .

# 3. Commit (les hooks s'exécutent automatiquement)
git commit -m "feat(email): add attachment support"

# ✅ Si tout passe :
# 🔍 Pre-commit hooks - ERP LITE
# 📋 Vérification des fichiers sensibles...
# ✓ Aucun fichier sensible détecté
# ☕ Backend Java - Vérification...
#   - Compilation du code...
# ✓ Backend Java OK
# ✅ Pre-commit checks passed!

# 4. Push
git push
```

## 💡 Conseils

- Les hooks ralentissent légèrement les commits mais **garantissent la qualité**
- Activez progressivement les vérifications selon vos besoins
- Utilisez `--no-verify` uniquement en cas d'urgence
- Les hooks ne sont **pas partagés via Git** - chaque développeur doit les installer

## 🔄 Installation pour d'autres développeurs

Les hooks sont déjà dans `.git/hooks/`. Si vous clonez le projet :

```bash
# Les hooks sont automatiquement présents dans .git/hooks/
# Ils sont déjà exécutables (chmod +x)
```

Pour partager les hooks avec l'équipe, créez un dossier `hooks/` à la racine :

```bash
mkdir hooks
cp .git/hooks/pre-commit hooks/
cp .git/hooks/commit-msg hooks/

# Puis chaque dev fait :
cp hooks/* .git/hooks/
chmod +x .git/hooks/*
```
