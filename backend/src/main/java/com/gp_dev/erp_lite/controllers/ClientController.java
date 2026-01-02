package com.gp_dev.erp_lite.controllers;

import com.gp_dev.erp_lite.dtos.ClientDto;
import com.gp_dev.erp_lite.dtos.CreateClientDto;
import com.gp_dev.erp_lite.dtos.UpdateClientDto;
import com.gp_dev.erp_lite.services.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = ClientController.REQUEST_MAPPING_NAME, produces = "application/json; charset=UTF-8")
public class ClientController {

    public static final String REQUEST_MAPPING_NAME = "/api/v1/clients";
    private final ClientService clientService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Page<ClientDto>> getAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestParam(required = false) String search) {
        
        Page<ClientDto> clients;
        if (search != null && !search.trim().isEmpty()) {
            clients = clientService.search(search.trim(), pageable);
        } else {
            clients = clientService.findAll(pageable);
        }
        
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ClientDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ClientDto> create(@Valid @RequestBody CreateClientDto createClientDto) {
        ClientDto created = clientService.create(createClientDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ClientDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClientDto updateClientDto) {
        return ResponseEntity.ok(clientService.update(id, updateClientDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
