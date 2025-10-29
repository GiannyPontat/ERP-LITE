package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.DevisDto;
import com.gp_dev.erp_lite.models.Devis;

import java.util.List;

public interface DevisService {
    List<DevisDto> all();

    Devis byId(Long id);

    Devis save(DevisDto devis);

    Devis update(DevisDto devis);

    void delete(Long id);
}
