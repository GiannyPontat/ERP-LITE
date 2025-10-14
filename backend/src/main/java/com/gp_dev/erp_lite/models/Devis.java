package com.gp_dev.erp_lite.models;

import com.gp_dev.erp_lite.dtos.DevisDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "gp_erp_devis")
public class Devis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Client client;

    private LocalDate dateCreation;
    private String statut;
    private double totalHT;
    private double totalTTC;

    @OneToMany(mappedBy = "devis")
    private List<LigneDevis> lignes;

    public DevisDto dto() {
        return DevisDto.builder()
                .id(id)
                .statut(statut)
                .dateCreation(dateCreation)
                .totalHT(totalHT)
                .totalTTC(totalTTC)
                .lignes(lignes == null ? List.of() :
                        lignes.stream().map(LigneDevis::dto).toList())
                .build();
    }
}
