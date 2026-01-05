package com.gp_dev.erp_lite.services.impl;

import com.gp_dev.erp_lite.dtos.CatalogItemDto;
import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.CatalogCategory;
import com.gp_dev.erp_lite.models.CatalogItem;
import com.gp_dev.erp_lite.repositories.CatalogItemRepo;
import com.gp_dev.erp_lite.services.CatalogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class CatalogServiceImpl implements CatalogService {

    private final CatalogItemRepo catalogItemRepo;

    @Override
    @Transactional(readOnly = true)
    public Page<CatalogItemDto> findAll(Pageable pageable) {
        return catalogItemRepo.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatalogItemDto> findAllActive(Pageable pageable) {
        return catalogItemRepo.findByActiveTrue(pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CatalogItemDto findById(Long id) {
        CatalogItem item = catalogItemRepo.findById(id)
                .orElseThrow(() -> new AppException("Catalog item not found", HttpStatus.NOT_FOUND));
        return toDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public CatalogItemDto findByReference(String reference) {
        CatalogItem item = catalogItemRepo.findByReference(reference)
                .orElseThrow(() -> new AppException("Catalog item not found with reference: " + reference, HttpStatus.NOT_FOUND));
        return toDto(item);
    }

    @Override
    public CatalogItemDto create(CatalogItemDto dto) {
        if (catalogItemRepo.existsByReference(dto.getReference())) {
            throw new AppException("Catalog item with reference " + dto.getReference() + " already exists", HttpStatus.CONFLICT);
        }

        CatalogItem item = CatalogItem.builder()
                .reference(dto.getReference().toUpperCase())
                .designation(dto.getDesignation())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .unit(dto.getUnit())
                .unitPrice(dto.getUnitPrice())
                .taxRate(dto.getTaxRate() != null ? dto.getTaxRate() : new BigDecimal("20.00"))
                .costPrice(dto.getCostPrice())
                .supplier(dto.getSupplier())
                .brand(dto.getBrand())
                .manufacturerReference(dto.getManufacturerReference())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .notes(dto.getNotes())
                .build();

        item = catalogItemRepo.save(item);
        log.info("Created catalog item: {}", item.getReference());
        return toDto(item);
    }

    @Override
    public CatalogItemDto update(Long id, CatalogItemDto dto) {
        CatalogItem item = catalogItemRepo.findById(id)
                .orElseThrow(() -> new AppException("Catalog item not found", HttpStatus.NOT_FOUND));

        // Vérifier l'unicité de la référence si modifiée
        if (dto.getReference() != null && !dto.getReference().equalsIgnoreCase(item.getReference())) {
            if (catalogItemRepo.existsByReference(dto.getReference())) {
                throw new AppException("Catalog item with reference " + dto.getReference() + " already exists", HttpStatus.CONFLICT);
            }
            item.setReference(dto.getReference().toUpperCase());
        }

        if (dto.getDesignation() != null) item.setDesignation(dto.getDesignation());
        if (dto.getDescription() != null) item.setDescription(dto.getDescription());
        if (dto.getCategory() != null) item.setCategory(dto.getCategory());
        if (dto.getUnit() != null) item.setUnit(dto.getUnit());
        if (dto.getUnitPrice() != null) item.setUnitPrice(dto.getUnitPrice());
        if (dto.getTaxRate() != null) item.setTaxRate(dto.getTaxRate());
        if (dto.getCostPrice() != null) item.setCostPrice(dto.getCostPrice());
        if (dto.getSupplier() != null) item.setSupplier(dto.getSupplier());
        if (dto.getBrand() != null) item.setBrand(dto.getBrand());
        if (dto.getManufacturerReference() != null) item.setManufacturerReference(dto.getManufacturerReference());
        if (dto.getActive() != null) item.setActive(dto.getActive());
        if (dto.getNotes() != null) item.setNotes(dto.getNotes());

        item = catalogItemRepo.save(item);
        log.info("Updated catalog item: {}", item.getReference());
        return toDto(item);
    }

    @Override
    public void delete(Long id) {
        if (!catalogItemRepo.existsById(id)) {
            throw new AppException("Catalog item not found", HttpStatus.NOT_FOUND);
        }
        catalogItemRepo.deleteById(id);
        log.info("Deleted catalog item with id: {}", id);
    }

    @Override
    public void toggleActive(Long id) {
        CatalogItem item = catalogItemRepo.findById(id)
                .orElseThrow(() -> new AppException("Catalog item not found", HttpStatus.NOT_FOUND));
        item.setActive(!item.getActive());
        catalogItemRepo.save(item);
        log.info("Toggled active status for catalog item: {} to {}", item.getReference(), item.getActive());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatalogItemDto> search(String search, Pageable pageable) {
        return catalogItemRepo.search(search, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatalogItemDto> searchActive(String search, Pageable pageable) {
        return catalogItemRepo.searchActive(search, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatalogItemDto> findByCategory(CatalogCategory category, Pageable pageable) {
        return catalogItemRepo.findByCategory(category, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatalogItemDto> searchByCategory(String search, CatalogCategory category, Pageable pageable) {
        return catalogItemRepo.searchByCategory(search, category, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findAllSuppliers() {
        return catalogItemRepo.findAllSuppliers();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findAllBrands() {
        return catalogItemRepo.findAllBrands();
    }

    @Override
    public List<CatalogCategory> getAllCategories() {
        return Arrays.asList(CatalogCategory.values());
    }

    @Override
    @Transactional(readOnly = true)
    public long countByCategory(CatalogCategory category) {
        return catalogItemRepo.countByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActive() {
        return catalogItemRepo.countByActiveTrue();
    }

    @Override
    public int importItems(List<CatalogItemDto> items) {
        int imported = 0;
        for (CatalogItemDto dto : items) {
            try {
                if (!catalogItemRepo.existsByReference(dto.getReference())) {
                    create(dto);
                    imported++;
                }
            } catch (Exception e) {
                log.warn("Failed to import item {}: {}", dto.getReference(), e.getMessage());
            }
        }
        log.info("Imported {} catalog items", imported);
        return imported;
    }

    private CatalogItemDto toDto(CatalogItem item) {
        CatalogItemDto dto = CatalogItemDto.builder()
                .id(item.getId())
                .reference(item.getReference())
                .designation(item.getDesignation())
                .description(item.getDescription())
                .category(item.getCategory())
                .categoryDisplayName(item.getCategory() != null ? item.getCategory().getDisplayName() : null)
                .unit(item.getUnit())
                .unitPrice(item.getUnitPrice())
                .taxRate(item.getTaxRate())
                .costPrice(item.getCostPrice())
                .supplier(item.getSupplier())
                .brand(item.getBrand())
                .manufacturerReference(item.getManufacturerReference())
                .active(item.getActive())
                .notes(item.getNotes())
                .userId(item.getUser() != null ? item.getUser().getId() : null)
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();

        // Calculer la marge
        if (item.getUnitPrice() != null && item.getCostPrice() != null && item.getCostPrice().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margin = item.getUnitPrice().subtract(item.getCostPrice());
            dto.setMargin(margin);
            dto.setMarginPercentage(margin.multiply(new BigDecimal("100"))
                    .divide(item.getCostPrice(), 2, RoundingMode.HALF_UP));
        }

        return dto;
    }
}

