# 🪝 Git Hooks - Installation rapide

## Installation automatique

```bash
./hooks/install.sh
```

## Installation manuelle

```bash
cp hooks/pre-commit .git/hooks/
cp hooks/commit-msg .git/hooks/
chmod +x .git/hooks/*
```

## Vérification

Testez que les hooks fonctionnent :

```bash
.git/hooks/pre-commit
```

## Documentation complète

Consultez [GIT_HOOKS.md](../GIT_HOOKS.md) à la racine du projet pour :
- Liste complète des vérifications
- Personnalisation des hooks
- Exemples d'utilisation
- Activation des fonctionnalités optionnelles

## Contenu

- **pre-commit** : Vérifie le code avant chaque commit
- **commit-msg** : Valide le format des messages de commit
- **install.sh** : Script d'installation automatique
