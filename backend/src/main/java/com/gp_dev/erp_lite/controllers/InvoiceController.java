package com.gp_dev.erp_lite.controllers;

import com.gp_dev.erp_lite.dtos.InvoiceDto;
import com.gp_dev.erp_lite.services.InvoiceService;
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
@RequestMapping(InvoiceController.REQUEST_MAPPING_NAME)
public class InvoiceController {

    public static final String REQUEST_MAPPING_NAME = "/api/v1/invoices";

    private final InvoiceService invoiceService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<InvoiceDto>> getAll() {
        return ResponseEntity.ok(invoiceService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<InvoiceDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceDto> create(@Valid @RequestBody InvoiceDto invoiceDto) {
        InvoiceDto created = invoiceService.create(invoiceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/from-quote/{quoteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceDto> createFromQuote(
            @PathVariable Long quoteId,
            @Valid @RequestBody InvoiceDto invoiceDto) {
        InvoiceDto created = invoiceService.createFromQuote(quoteId, invoiceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceDto> update(@PathVariable Long id, @Valid @RequestBody InvoiceDto invoiceDto) {
        return ResponseEntity.ok(invoiceService.update(id, invoiceDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<InvoiceDto>> getByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(invoiceService.findByClientId(clientId));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<InvoiceDto>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(invoiceService.findByStatus(status));
    }
}

