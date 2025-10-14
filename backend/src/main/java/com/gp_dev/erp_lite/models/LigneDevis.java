package com.gp_dev.erp_lite.models;

import com.gp_dev.erp_lite.dtos.LigneDevisDto;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "gp_erp_lignedevis")
public class LigneDevis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Devis devis;

    private String description;
    private int quantite;
    private double prixUnitaire;

    public LigneDevisDto dto() {
        return new LigneDevisDto(
                id,
                description,
                quantite,
                prixUnitaire
        );
    }
}
