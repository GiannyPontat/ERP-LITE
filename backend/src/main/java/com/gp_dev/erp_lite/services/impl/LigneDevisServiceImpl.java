package com.gp_dev.erp_lite.services.impl;

import com.gp_dev.erp_lite.dtos.LigneDevisDto;
import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.LigneDevis;
import com.gp_dev.erp_lite.repositories.LigneDevisRepo;
import com.gp_dev.erp_lite.services.LigneDevisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class LigneDevisServiceImpl implements LigneDevisService {

    private final LigneDevisRepo repository;

    @Override
    public List<LigneDevisDto> all() {
        return repository.findAll().stream()
                .map(LigneDevis::dto)
                .collect(Collectors.toList());
    }

    @Override
    public LigneDevis byId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AppException("Ligne devis not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public LigneDevis save(LigneDevisDto dto) {
        LigneDevis ligneDevis = LigneDevis.builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .quantite(dto.getQuantite())
                .prixUnitaire(dto.getPrixUnitaire())
                .build();
        return repository.saveAndFlush(ligneDevis);
    }

    @Override
    public LigneDevis update(LigneDevisDto dto) {
        LigneDevis ligneDevis = byId(dto.getId());
        ligneDevis.setDescription(dto.getDescription());
        ligneDevis.setQuantite(dto.getQuantite());
        ligneDevis.setPrixUnitaire(dto.getPrixUnitaire());
        return repository.saveAndFlush(ligneDevis);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
