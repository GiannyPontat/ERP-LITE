#!/bin/bash

# Script pour lancer l'application avec les variables d'environnement

# Charger les variables depuis .env si le fichier existe
if [ -f .env ]; then
    echo "📧 Chargement des variables d'environnement depuis .env..."
    export $(cat .env | grep -v '^#' | xargs)
else
    echo "⚠️  Fichier .env non trouvé. Copiez .env.example vers .env et configurez vos identifiants."
    exit 1
fi

# Lancer l'application
echo "🚀 Lancement de l'application..."
./mvnw spring-boot:run
