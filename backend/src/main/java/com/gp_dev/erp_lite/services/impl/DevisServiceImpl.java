package com.gp_dev.erp_lite.services.impl;

import com.gp_dev.erp_lite.dtos.DevisDto;
import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.Devis;
import com.gp_dev.erp_lite.repositories.DevisRepo;
import com.gp_dev.erp_lite.services.DevisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class DevisServiceImpl implements DevisService {

    private final DevisRepo repository;

    @Override
    public List<DevisDto> all() {
        return this.repository.findAll().stream()
                .map(Devis::dto)
                .collect(Collectors.toList());
    }

    @Override
    public Devis byId(Long id) {
        return this.repository.findById(id)
                .orElseThrow(() -> new AppException("Devis not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public Devis save(DevisDto devis) {
        return this.repository.saveAndFlush(Devis.builder()
                ./* map fields from devis dto to Devis model */
                        build());
    }

    @Override
    public Devis update(DevisDto devis) {
        Devis model = this.repository.findById(devis.getId())
                .orElseThrow(() -> new AppException("Devis not found", HttpStatus.NOT_FOUND));

        // set fields from devis dto to model
        return this.repository.save(model);
    }

    @Override
    public void delete(Long id) {
        this.repository.deleteById(id);
    }
}
