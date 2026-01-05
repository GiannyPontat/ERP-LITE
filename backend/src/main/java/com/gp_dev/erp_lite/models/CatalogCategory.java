package com.gp_dev.erp_lite.models;

/**
 * Catégories pour les prestations plomberie
 */
public enum CatalogCategory {
    DEPANNAGE("Dépannage & Urgence"),
    ROBINETTERIE("Robinetterie"),
    SANITAIRES("Sanitaires (WC, lavabo)"),
    CHAUFFE_EAU("Chauffe-eau & Ballon"),
    CHAUDIERE("Chaudière & Chauffage"),
    CANALISATIONS("Canalisations & Évacuation"),
    SALLE_DE_BAIN("Salle de bain complète"),
    CUISINE("Cuisine (évier, lave-vaisselle)"),
    EXTERIEUR("Extérieur (arrosage, piscine)"),
    MAIN_OEUVRE("Main d'œuvre"),
    DEPLACEMENT("Déplacement"),
    DIVERS("Divers");

    private final String displayName;

    CatalogCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

