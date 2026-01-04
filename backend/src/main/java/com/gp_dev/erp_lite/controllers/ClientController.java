package com.gp_dev.erp_lite.controllers;

import com.gp_dev.erp_lite.dtos.ClientDto;
import com.gp_dev.erp_lite.dtos.CreateClientDto;
import com.gp_dev.erp_lite.dtos.ErrorResponse;
import com.gp_dev.erp_lite.dtos.UpdateClientDto;
import com.gp_dev.erp_lite.services.ClientService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Clients", description = "Client/customer management endpoints")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = ClientController.REQUEST_MAPPING_NAME, produces = "application/json; charset=UTF-8")
public class ClientController {

    public static final String REQUEST_MAPPING_NAME = "/api/v1/clients";
    private final ClientService clientService;

    @Operation(summary = "Get all clients", description = "Retrieves paginated list of clients with optional search",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clients retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
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

    @Operation(summary = "Get client by ID", description = "Retrieves detailed information about a specific client",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Client found",
            content = @Content(schema = @Schema(implementation = ClientDto.class))),
        @ApiResponse(responseCode = "404", description = "Client not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ClientDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    @Operation(summary = "Create new client", description = "Creates a new client record",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Client created successfully",
            content = @Content(schema = @Schema(implementation = ClientDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ClientDto> create(@Valid @RequestBody CreateClientDto createClientDto) {
        ClientDto created = clientService.create(createClientDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Update client", description = "Updates an existing client's information",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Client updated successfully",
            content = @Content(schema = @Schema(implementation = ClientDto.class))),
        @ApiResponse(responseCode = "404", description = "Client not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ClientDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClientDto updateClientDto) {
        return ResponseEntity.ok(clientService.update(id, updateClientDto));
    }

    @Operation(summary = "Delete client", description = "Deletes a client record",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Client deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Client not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
