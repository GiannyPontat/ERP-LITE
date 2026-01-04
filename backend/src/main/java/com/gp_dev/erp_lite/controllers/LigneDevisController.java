package com.gp_dev.erp_lite.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
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

@Tag(name = "Legacy - LigneDevis (Deprecated)", description = "Legacy endpoints - Use Quote Items within Quotes API (/api/v1/quotes) instead. Will be removed in future versions.")
@Deprecated
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(LigneDevisController.REQUEST_MAPPING_NAME)
public class LigneDevisController {

    public static final String REQUEST_MAPPING_NAME = "/api/v1/lignes-devis";

    private final LigneDevisService service;

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<LigneDevisDto> getAll() {
        return service.all();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public LigneDevisDto getById(@PathVariable Long id) {
        return service.byId(id).dto();
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public LigneDevisDto save(@Valid @RequestBody LigneDevisDto dto) {
        return service.save(dto).dto();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public LigneDevisDto update(@Valid @RequestBody LigneDevisDto dto) {
        return service.update(dto).dto();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
