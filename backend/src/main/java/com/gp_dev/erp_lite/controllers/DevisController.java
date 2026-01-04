package com.gp_dev.erp_lite.controllers;

import com.gp_dev.erp_lite.dtos.DevisDto;
import com.gp_dev.erp_lite.services.DevisService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "Legacy - Devis (Deprecated)", description = "Legacy endpoints - Use Quotes API (/api/v1/quotes) instead. Will be removed in future versions.")
@Deprecated
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(DevisController.REQUEST_MAPPING_NAME)
public class DevisController {

    public static final String REQUEST_MAPPING_NAME = "/api/v1/devis";

    private final DevisService service;

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<DevisDto> getAll() {
        return service.all();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public DevisDto getById(@PathVariable Long id) {
        return service.byId(id).dto();
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public DevisDto save(@Valid @RequestBody DevisDto dto) {
        return service.save(dto).dto();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DevisDto update(@PathVariable Long id, @Valid @RequestBody DevisDto dto) {
        return service.update(dto).dto();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
