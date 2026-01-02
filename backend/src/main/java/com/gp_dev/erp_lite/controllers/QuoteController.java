package com.gp_dev.erp_lite.controllers;

import com.gp_dev.erp_lite.dtos.QuoteDto;
import com.gp_dev.erp_lite.services.QuoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(QuoteController.REQUEST_MAPPING_NAME)
public class QuoteController {

    public static final String REQUEST_MAPPING_NAME = "/api/v1/quotes";

    private final QuoteService quoteService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<QuoteDto>> getAll() {
        return ResponseEntity.ok(quoteService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<QuoteDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(quoteService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuoteDto> create(@Valid @RequestBody QuoteDto quoteDto) {
        QuoteDto created = quoteService.create(quoteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuoteDto> update(@PathVariable Long id, @Valid @RequestBody QuoteDto quoteDto) {
        return ResponseEntity.ok(quoteService.update(id, quoteDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        quoteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<QuoteDto>> getByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(quoteService.findByClientId(clientId));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<QuoteDto>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(quoteService.findByStatus(status));
    }
}

