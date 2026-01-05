package com.gp_dev.erp_lite.repositories;

import com.gp_dev.erp_lite.models.CatalogCategory;
import com.gp_dev.erp_lite.models.CatalogItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogItemRepo extends JpaRepository<CatalogItem, Long> {

    Optional<CatalogItem> findByReference(String reference);

    boolean existsByReference(String reference);

    @Query("SELECT c FROM CatalogItem c WHERE c.reference = :reference AND c.id != :excludeId")
    Optional<CatalogItem> findByReferenceExcludingId(@Param("reference") String reference, @Param("excludeId") Long excludeId);

    List<CatalogItem> findByCategory(CatalogCategory category);

    Page<CatalogItem> findByCategory(CatalogCategory category, Pageable pageable);

    List<CatalogItem> findByActiveTrue();

    Page<CatalogItem> findByActiveTrue(Pageable pageable);

    @Query("SELECT c FROM CatalogItem c WHERE c.active = true AND " +
           "(LOWER(c.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.designation) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CatalogItem> searchActive(@Param("search") String search, Pageable pageable);

    @Query("SELECT c FROM CatalogItem c WHERE " +
           "LOWER(c.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.designation) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<CatalogItem> search(@Param("search") String search, Pageable pageable);

    @Query("SELECT c FROM CatalogItem c WHERE c.active = true AND c.category = :category AND " +
           "(LOWER(c.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.designation) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CatalogItem> searchByCategory(@Param("search") String search, 
                                        @Param("category") CatalogCategory category, 
                                        Pageable pageable);

    List<CatalogItem> findBySupplierIgnoreCase(String supplier);

    List<CatalogItem> findByBrandIgnoreCase(String brand);

    @Query("SELECT DISTINCT c.supplier FROM CatalogItem c WHERE c.supplier IS NOT NULL ORDER BY c.supplier")
    List<String> findAllSuppliers();

    @Query("SELECT DISTINCT c.brand FROM CatalogItem c WHERE c.brand IS NOT NULL ORDER BY c.brand")
    List<String> findAllBrands();

    long countByCategory(CatalogCategory category);

    long countByActiveTrue();
}

