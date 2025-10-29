package com.gp_dev.erp_lite.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import java.util.List;
import com.gp_dev.erp_lite.services.LigneDevisService;
import com.gp_dev.erp_lite.dtos.LigneDevisDto;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(LigneDevisController.REQUEST_MAPPING_NAME)
public class LigneDevisController {

    public static final String REQUEST_MAPPING_NAME = "/api/v1/lignes-devis";

    private final LigneDevisService service;

    @GetMapping()
    public List<LigneDevisDto> getAll() {
        return service.all();
    }

    @GetMapping("/{id}")
    public LigneDevisDto getById(@PathVariable Long id) {
        return service.byId(id).dto();
    }

    @PostMapping()
    public LigneDevisDto save(@Valid @RequestBody LigneDevisDto dto) {
        return service.save(dto).dto();
    }

    @PutMapping("/{id}")
    public LigneDevisDto update(@Valid @RequestBody LigneDevisDto dto) {
        return service.update(dto).dto();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
