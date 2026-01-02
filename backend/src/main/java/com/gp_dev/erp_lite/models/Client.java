package com.gp_dev.erp_lite.models;

import com.gp_dev.erp_lite.dtos.ClientDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "gp_erp_client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Gardé en Long pour compatibilité
    
    // Nouveaux champs selon spec
    @Column(name = "company_name")
    private String companyName;

    private String siret;

    @Column(name = "contact_first_name")
    private String contactFirstName;

    @Column(name = "contact_last_name")
    private String contactLastName;

    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    private String city;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "payment_terms")
    private Integer paymentTerms; // en jours

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Champs pour compatibilité avec code existant
    @Column(name = "nom")
    private String nom; // Pour compatibilité avec ClientDto

    @Column(name = "entreprise")
    private String entreprise; // Pour compatibilité avec ClientDto

    @Column(name = "telephone")
    private String telephone; // Pour compatibilité avec ClientDto

    @Column(name = "adresse")
    private String adresse; // Pour compatibilité avec ClientDto

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Relation ManyToOne avec User selon spec

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quote> quotes;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invoice> invoices;

    @OneToMany(mappedBy = "client")
    private List<Devis> devis; // Pour compatibilité avec code existant

    // Méthode pour compatibilité
    public ClientDto dto() {
        return ClientDto.builder()
                .id(id)
                .companyName(companyName)
                .siret(siret)
                .contactFirstName(contactFirstName)
                .contactLastName(contactLastName)
                .email(email)
                .phone(phone)
                .address(address)
                .city(city)
                .postalCode(postalCode)
                .paymentTerms(paymentTerms)
                .notes(notes)
                .userId(user != null ? user.getId() : null)
                // Champs de compatibilité
                .nom(nom != null ? nom : contactLastName)
                .entreprise(entreprise != null ? entreprise : companyName)
                .telephone(telephone != null ? telephone : phone)
                .adresse(adresse != null ? adresse : address)
                .build();
    }
}
