package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.*;

import java.util.List;

public interface ClientPortalService {

    /**
     * Authentification client portail
     */
    ClientPortalAuthResponse login(ClientPortalLoginRequest request);

    /**
     * Créer un accès portail pour un client (par admin)
     */
    void createPortalAccess(ClientPortalRegisterRequest request);

    /**
     * Vérifier l'email du portail client
     */
    void verifyEmail(String token);

    /**
     * Demande de reset password
     */
    void requestPasswordReset(String email);

    /**
     * Reset password
     */
    void resetPassword(String token, String newPassword);

    /**
     * Changer le mot de passe
     */
    void changePassword(Long portalAccessId, String oldPassword, String newPassword);

    /**
     * Récupérer les devis du client
     */
    List<ClientPortalQuoteDto> getClientQuotes(Long clientId);

    /**
     * Récupérer un devis par ID (avec vérification du client)
     */
    ClientPortalQuoteDto getQuote(Long clientId, Long quoteId);

    /**
     * Accepter un devis
     */
    ClientPortalQuoteDto acceptQuote(Long clientId, Long quoteId);

    /**
     * Rejeter un devis
     */
    ClientPortalQuoteDto rejectQuote(Long clientId, Long quoteId);

    /**
     * Récupérer les factures du client
     */
    List<ClientPortalInvoiceDto> getClientInvoices(Long clientId);

    /**
     * Récupérer une facture par ID (avec vérification du client)
     */
    ClientPortalInvoiceDto getInvoice(Long clientId, Long invoiceId);

    /**
     * Télécharger le PDF d'un devis
     */
    byte[] downloadQuotePdf(Long clientId, Long quoteId);

    /**
     * Télécharger le PDF d'une facture
     */
    byte[] downloadInvoicePdf(Long clientId, Long invoiceId);

    /**
     * Récupérer les projets du client
     */
    List<ProjectDto> getClientProjects(Long clientId);

    /**
     * Récupérer un projet par ID
     */
    ProjectDto getProject(Long clientId, Long projectId);

    /**
     * Vérifier si un client a accès au portail
     */
    boolean hasPortalAccess(Long clientId);

    /**
     * Désactiver l'accès portail
     */
    void disablePortalAccess(Long clientId);

    /**
     * Réactiver l'accès portail
     */
    void enablePortalAccess(Long clientId);
}

