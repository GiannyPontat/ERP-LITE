package com.gp_dev.erp_lite.controllers;

import com.gp_dev.erp_lite.dtos.ErrorResponse;
import com.gp_dev.erp_lite.dtos.InvoiceDto;
import com.gp_dev.erp_lite.services.EmailService;
import com.gp_dev.erp_lite.services.InvoiceService;
import com.gp_dev.erp_lite.services.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Invoices", description = "Invoice management endpoints")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(InvoiceController.REQUEST_MAPPING_NAME)
public class InvoiceController {

    public static final String REQUEST_MAPPING_NAME = "/api/v1/invoices";

    private final InvoiceService invoiceService;
    private final PdfService pdfService;
    private final EmailService emailService;

    @Operation(summary = "Get all invoices", description = "Retrieves list of all invoices",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Invoices retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<InvoiceDto>> getAll() {
        return ResponseEntity.ok(invoiceService.findAll());
    }

    @Operation(summary = "Get invoice by ID", description = "Retrieves detailed information about a specific invoice including line items",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Invoice found",
            content = @Content(schema = @Schema(implementation = InvoiceDto.class))),
        @ApiResponse(responseCode = "404", description = "Invoice not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<InvoiceDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.findById(id));
    }

    @Operation(summary = "Create new invoice", description = "Creates a new invoice with line items and auto-generates invoice number",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Invoice created successfully",
            content = @Content(schema = @Schema(implementation = InvoiceDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceDto> create(@Valid @RequestBody InvoiceDto invoiceDto) {
        InvoiceDto created = invoiceService.create(invoiceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Create invoice from quote", description = "Converts an accepted quote into an invoice, copying all quote details and items",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Invoice created from quote successfully",
            content = @Content(schema = @Schema(implementation = InvoiceDto.class))),
        @ApiResponse(responseCode = "404", description = "Quote not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Quote cannot be converted (invalid status)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/from-quote/{quoteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceDto> createFromQuote(
            @PathVariable Long quoteId,
            @Valid @RequestBody InvoiceDto invoiceDto) {
        InvoiceDto created = invoiceService.createFromQuote(quoteId, invoiceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Update invoice", description = "Updates an existing invoice and its line items",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Invoice updated successfully",
            content = @Content(schema = @Schema(implementation = InvoiceDto.class))),
        @ApiResponse(responseCode = "404", description = "Invoice not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceDto> update(@PathVariable Long id, @Valid @RequestBody InvoiceDto invoiceDto) {
        return ResponseEntity.ok(invoiceService.update(id, invoiceDto));
    }

    @Operation(summary = "Delete invoice", description = "Deletes an invoice and all associated line items",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Invoice deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Invoice not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get invoices by client", description = "Retrieves all invoices for a specific client",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Invoices retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Client not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<InvoiceDto>> getByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(invoiceService.findByClientId(clientId));
    }

    @Operation(summary = "Get invoices by status", description = "Retrieves all invoices with a specific status (DRAFT, SENT, PAID, OVERDUE, CANCELLED, PARTIALLY_PAID)",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Invoices retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<InvoiceDto>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(invoiceService.findByStatus(status));
    }

    @Operation(summary = "Generate PDF for invoice", description = "Generates a PDF document for the specified invoice",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF generated successfully",
            content = @Content(mediaType = "application/pdf")),
        @ApiResponse(responseCode = "404", description = "Invoice not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        log.info("Generate PDF request for invoice ID: {}", id);
        InvoiceDto invoiceDto = invoiceService.findById(id);
        byte[] pdfBytes = pdfService.generateInvoicePdf(invoiceDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"facture-" + invoiceDto.getInvoiceNumber() + ".pdf\"");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @Operation(summary = "Send invoice by email", description = "Sends the invoice PDF to the specified email address",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email sent successfully"),
        @ApiResponse(responseCode = "404", description = "Invoice not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid email address",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/{id}/send-email")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<String> sendInvoiceEmail(@PathVariable Long id, @RequestParam String email) {
        log.info("Send email request for invoice ID: {} to: {}", id, email);
        InvoiceDto invoiceDto = invoiceService.findById(id);
        emailService.sendInvoiceEmail(invoiceDto, email);
        return ResponseEntity.ok("Facture envoyée avec succès à " + email);
    }
}
