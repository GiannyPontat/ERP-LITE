package com.gp_dev.erp_lite.controllers;

import com.gp_dev.erp_lite.dtos.DevisDto;
import com.gp_dev.erp_lite.services.DevisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(DevisController.REQUEST_MAPPING_NAME)
public class DevisController {

    public static final String REQUEST_MAPPING_NAME = "/api/v1/devis";

    private final DevisService service;

    @GetMapping()
    public List<DevisDto> getAll() {
        return service.all();
    }

    @GetMapping("/{id}")
    public DevisDto getById(@PathVariable Long id) {
        return service.byId(id).dto();
    }

    @PostMapping()
    public DevisDto save(@Valid @RequestBody DevisDto dto) {
        return service.save(dto).dto();
    }

    @PutMapping("/{id}")
    public DevisDto update(@PathVariable Long id, @Valid @RequestBody DevisDto dto) {
        return service.update(dto).dto();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
