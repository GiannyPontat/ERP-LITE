package com.gp_dev.erp_lite.controllers;

import com.gp_dev.erp_lite.dtos.CatalogItemDto;
import com.gp_dev.erp_lite.dtos.ErrorResponse;
import com.gp_dev.erp_lite.models.CatalogCategory;
import com.gp_dev.erp_lite.services.CatalogService;
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

import java.util.List;

@Tag(name = "Catalog", description = "BTP Price catalog management endpoints")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    @Operation(summary = "Get all catalog items", description = "Retrieves paginated list of all catalog items",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Items retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<CatalogItemDto>> getAll(
            @PageableDefault(size = 20, sort = "reference") Pageable pageable) {
        return ResponseEntity.ok(catalogService.findAll(pageable));
    }

    @Operation(summary = "Get active catalog items", description = "Retrieves paginated list of active catalog items only",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<CatalogItemDto>> getAllActive(
            @PageableDefault(size = 20, sort = "reference") Pageable pageable) {
        return ResponseEntity.ok(catalogService.findAllActive(pageable));
    }

    @Operation(summary = "Get catalog item by ID", description = "Retrieves detailed information about a specific catalog item",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item found",
            content = @Content(schema = @Schema(implementation = CatalogItemDto.class))),
        @ApiResponse(responseCode = "404", description = "Item not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CatalogItemDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.findById(id));
    }

    @Operation(summary = "Get catalog item by reference", description = "Retrieves a catalog item by its reference code",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/reference/{reference}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CatalogItemDto> getByReference(@PathVariable String reference) {
        return ResponseEntity.ok(catalogService.findByReference(reference));
    }

    @Operation(summary = "Create catalog item", description = "Creates a new catalog item",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Item created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Reference already exists")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CatalogItemDto> create(@Valid @RequestBody CatalogItemDto catalogItemDto) {
        CatalogItemDto created = catalogService.create(catalogItemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Update catalog item", description = "Updates an existing catalog item",
        security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CatalogItemDto> update(@PathVariable Long id, @Valid @RequestBody CatalogItemDto catalogItemDto) {
        return ResponseEntity.ok(catalogService.update(id, catalogItemDto));
    }

    @Operation(summary = "Delete catalog item", description = "Deletes a catalog item",
        security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        catalogService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Toggle item active status", description = "Activates or deactivates a catalog item",
        security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> toggleActive(@PathVariable Long id) {
        catalogService.toggleActive(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Search catalog items", description = "Search items by reference, designation or description",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<CatalogItemDto>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "true") boolean activeOnly,
            @PageableDefault(size = 20) Pageable pageable) {
        if (activeOnly) {
            return ResponseEntity.ok(catalogService.searchActive(query, pageable));
        }
        return ResponseEntity.ok(catalogService.search(query, pageable));
    }

    @Operation(summary = "Get items by category", description = "Retrieves all items in a specific category",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<CatalogItemDto>> getByCategory(
            @PathVariable CatalogCategory category,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(catalogService.findByCategory(category, pageable));
    }

    @Operation(summary = "Search items in category", description = "Search items within a specific category",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/category/{category}/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<CatalogItemDto>> searchByCategory(
            @PathVariable CatalogCategory category,
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(catalogService.searchByCategory(query, category, pageable));
    }

    @Operation(summary = "Get all categories", description = "Retrieves all available catalog categories",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<CatalogCategory>> getAllCategories() {
        return ResponseEntity.ok(catalogService.getAllCategories());
    }

    @Operation(summary = "Get all suppliers", description = "Retrieves list of all suppliers in catalog",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/suppliers")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<String>> getAllSuppliers() {
        return ResponseEntity.ok(catalogService.findAllSuppliers());
    }

    @Operation(summary = "Get all brands", description = "Retrieves list of all brands in catalog",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/brands")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<String>> getAllBrands() {
        return ResponseEntity.ok(catalogService.findAllBrands());
    }

    @Operation(summary = "Import catalog items", description = "Bulk import of catalog items",
        security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Integer> importItems(@Valid @RequestBody List<CatalogItemDto> items) {
        int imported = catalogService.importItems(items);
        return ResponseEntity.ok(imported);
    }

    @Operation(summary = "Get catalog statistics", description = "Retrieves statistics about the catalog",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CatalogStatsResponse> getStats() {
        long totalActive = catalogService.countActive();
        return ResponseEntity.ok(new CatalogStatsResponse(totalActive));
    }

    public record CatalogStatsResponse(long totalActiveItems) {}
}

