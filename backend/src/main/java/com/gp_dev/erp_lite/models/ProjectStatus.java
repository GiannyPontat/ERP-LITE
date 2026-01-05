package com.gp_dev.erp_lite.models;

/**
 * Statuts possibles pour un chantier/projet
 */
public enum ProjectStatus {
    DRAFT("Brouillon"),
    PLANNING("En planification"),
    IN_PROGRESS("En cours"),
    ON_HOLD("En pause"),
    COMPLETED("Terminé"),
    CANCELLED("Annulé");

    private final String displayName;

    ProjectStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

