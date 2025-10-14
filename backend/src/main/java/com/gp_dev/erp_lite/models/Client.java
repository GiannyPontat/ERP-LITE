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
    private Long id;

    private String nom;
    private String entreprise;
    private String email;
    private String telephone;
    private String adresse;

    @OneToMany(mappedBy = "client")
    private List<Devis> devis;

    public ClientDto dto() {
        return new ClientDto(
                id,
                nom,
                entreprise,
                email,
                telephone,
                adresse
        );
    }
}
