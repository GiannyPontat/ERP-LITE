#!/bin/bash

echo "📦 Installation des Git hooks pour ERP-LITE..."

# Copier les hooks
cp hooks/pre-commit .git/hooks/
cp hooks/commit-msg .git/hooks/

# Rendre les hooks exécutables
chmod +x .git/hooks/pre-commit
chmod +x .git/hooks/commit-msg

echo "✅ Hooks installés avec succès!"
echo ""
echo "Fichiers installés:"
echo "  - .git/hooks/pre-commit"
echo "  - .git/hooks/commit-msg"
echo ""
echo "📖 Consultez GIT_HOOKS.md pour plus d'informations"
