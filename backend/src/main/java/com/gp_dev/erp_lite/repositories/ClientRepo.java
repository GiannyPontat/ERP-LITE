package com.gp_dev.erp_lite.repositories;

import com.gp_dev.erp_lite.models.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepo extends JpaRepository<Client, Long> {
    
    // Recherche par nom (contactFirstName ou contactLastName ou nom)
    @Query("SELECT c FROM Client c WHERE " +
           "LOWER(c.contactFirstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.contactLastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.companyName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Client> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    // Recherche par SIRET
    Optional<Client> findBySiret(String siret);
    
    // Recherche par email
    Optional<Client> findByEmail(String email);
    
    // Vérifier si SIRET existe (sauf pour un client donné)
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Client c WHERE c.siret = :siret AND c.id != :excludeId")
    boolean existsBySiretExcludingId(@Param("siret") String siret, @Param("excludeId") Long excludeId);
    
    // Vérifier si email existe (sauf pour un client donné)
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Client c WHERE c.email = :email AND c.id != :excludeId")
    boolean existsByEmailExcludingId(@Param("email") String email, @Param("excludeId") Long excludeId);
}
