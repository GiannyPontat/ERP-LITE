package com.gp_dev.erp_lite.controllers;

import com.gp_dev.erp_lite.dtos.ClientDto;
import com.gp_dev.erp_lite.services.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = ClientController.REQUEST_MAPPING_NAME, produces = "application/json; charset=UTF-8")
public class ClientController {

    public static final String REQUEST_MAPPING_NAME = "/api/v1/clients";
    private final ClientService service;

    @GetMapping()
    public ResponseEntity<List<ClientDto>> all() {
        return ResponseEntity.ok(service.all());
    }


    @GetMapping("/{idClient}")
    public ResponseEntity<ClientDto> get(@PathVariable Long idClient) {
        return ResponseEntity.ok(this.service.byId(idClient).dto());
    }


    @PostMapping()
    public ClientDto save(@Valid @RequestBody ClientDto client) {
        return this.service.save(client).dto();
    }


    @DeleteMapping("/{idClient}")
    public void delete(@PathVariable Long idClient) {
        this.service.delete(idClient);
    }


    @PutMapping("/{idClient}")
    public ClientDto update(@Valid @RequestBody ClientDto client) {
        return this.service.update(client).dto();
    }
}
