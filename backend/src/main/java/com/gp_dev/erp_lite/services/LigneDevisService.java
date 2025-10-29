package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.LigneDevisDto;
import com.gp_dev.erp_lite.models.LigneDevis;

import java.util.List;

public interface LigneDevisService {
    List<LigneDevisDto> all();

    LigneDevis byId(Long id);

    LigneDevis save(LigneDevisDto ligneDevis);

    LigneDevis update(LigneDevisDto ligneDevis);

    void delete(Long id);
}
