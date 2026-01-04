package com.gp_dev.erp_lite.controllers;

import com.gp_dev.erp_lite.dtos.ErrorResponse;
import com.gp_dev.erp_lite.dtos.InvoiceDto;
import com.gp_dev.erp_lite.dtos.QuoteDto;
import com.gp_dev.erp_lite.services.EmailService;
import com.gp_dev.erp_lite.services.PdfService;
import com.gp_dev.erp_lite.services.QuoteService;
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

@Tag(name = "Quotes", description = "Quote/estimate management endpoints")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(QuoteController.REQUEST_MAPPING_NAME)
public class QuoteController {

    public static final String REQUEST_MAPPING_NAME = "/api/v1/quotes";

    private final QuoteService quoteService;
    private final PdfService pdfService;
    private final EmailService emailService;

    @Operation(summary = "Get all quotes", description = "Retrieves list of all quotes",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quotes retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<QuoteDto>> getAll() {
        return ResponseEntity.ok(quoteService.findAll());
    }

    @Operation(summary = "Get quote by ID", description = "Retrieves detailed information about a specific quote including line items",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quote found",
            content = @Content(schema = @Schema(implementation = QuoteDto.class))),
        @ApiResponse(responseCode = "404", description = "Quote not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<QuoteDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(quoteService.findById(id));
    }

    @Operation(summary = "Create new quote", description = "Creates a new quote with line items and auto-generates quote number",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Quote created successfully",
            content = @Content(schema = @Schema(implementation = QuoteDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuoteDto> create(@Valid @RequestBody QuoteDto quoteDto) {
        QuoteDto created = quoteService.create(quoteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Update quote", description = "Updates an existing quote and its line items",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quote updated successfully",
            content = @Content(schema = @Schema(implementation = QuoteDto.class))),
        @ApiResponse(responseCode = "404", description = "Quote not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuoteDto> update(@PathVariable Long id, @Valid @RequestBody QuoteDto quoteDto) {
        return ResponseEntity.ok(quoteService.update(id, quoteDto));
    }

    @Operation(summary = "Delete quote", description = "Deletes a quote and all associated line items",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Quote deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Quote not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        quoteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get quotes by client", description = "Retrieves all quotes for a specific client",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quotes retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Client not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<QuoteDto>> getByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(quoteService.findByClientId(clientId));
    }

    @Operation(summary = "Get quotes by status", description = "Retrieves all quotes with a specific status (DRAFT, SENT, ACCEPTED, REJECTED, EXPIRED, CONVERTED)",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quotes retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<QuoteDto>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(quoteService.findByStatus(status));
    }

    @Operation(summary = "Generate PDF for quote", description = "Generates a PDF document for the specified quote",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF generated successfully",
            content = @Content(mediaType = "application/pdf")),
        @ApiResponse(responseCode = "404", description = "Quote not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        log.info("Generate PDF request for quote ID: {}", id);
        QuoteDto quoteDto = quoteService.findById(id);
        byte[] pdfBytes = pdfService.generateQuotePdf(quoteDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"devis-" + quoteDto.getQuoteNumber() + ".pdf\"");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @Operation(summary = "Send quote by email", description = "Sends the quote PDF to the specified email address",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email sent successfully"),
        @ApiResponse(responseCode = "404", description = "Quote not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid email address",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/{id}/send-email")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<String> sendQuoteEmail(@PathVariable Long id, @RequestParam String email) {
        log.info("Send email request for quote ID: {} to: {}", id, email);
        QuoteDto quoteDto = quoteService.findById(id);
        emailService.sendQuoteEmail(quoteDto, email);
        return ResponseEntity.ok("Devis envoyé avec succès à " + email);
    }

    @Operation(summary = "Convert quote to invoice", description = "Converts an accepted quote into an invoice",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Quote converted successfully",
            content = @Content(schema = @Schema(implementation = InvoiceDto.class))),
        @ApiResponse(responseCode = "404", description = "Quote not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Quote status invalid for conversion",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/{id}/convert-to-invoice")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceDto> convertToInvoice(@PathVariable Long id) {
        log.info("Convert to invoice request for quote ID: {}", id);
        InvoiceDto invoiceDto = quoteService.convertToInvoice(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceDto);
    }
}

