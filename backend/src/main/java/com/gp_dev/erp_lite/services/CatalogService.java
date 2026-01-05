package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.CatalogItemDto;
import com.gp_dev.erp_lite.models.CatalogCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CatalogService {

    Page<CatalogItemDto> findAll(Pageable pageable);

    Page<CatalogItemDto> findAllActive(Pageable pageable);

    CatalogItemDto findById(Long id);

    CatalogItemDto findByReference(String reference);

    CatalogItemDto create(CatalogItemDto catalogItemDto);

    CatalogItemDto update(Long id, CatalogItemDto catalogItemDto);

    void delete(Long id);

    void toggleActive(Long id);

    Page<CatalogItemDto> search(String search, Pageable pageable);

    Page<CatalogItemDto> searchActive(String search, Pageable pageable);

    Page<CatalogItemDto> findByCategory(CatalogCategory category, Pageable pageable);

    Page<CatalogItemDto> searchByCategory(String search, CatalogCategory category, Pageable pageable);

    List<String> findAllSuppliers();

    List<String> findAllBrands();

    List<CatalogCategory> getAllCategories();

    long countByCategory(CatalogCategory category);

    long countActive();

    /**
     * Import bulk catalog items (for initial data load)
     */
    int importItems(List<CatalogItemDto> items);
}

