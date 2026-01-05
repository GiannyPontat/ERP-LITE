package com.gp_dev.erp_lite.repositories;

import com.gp_dev.erp_lite.models.Project;
import com.gp_dev.erp_lite.models.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepo extends JpaRepository<Project, Long> {

    Optional<Project> findByReference(String reference);

    boolean existsByReference(String reference);

    List<Project> findByClientId(Long clientId);

    Page<Project> findByClientId(Long clientId, Pageable pageable);

    List<Project> findByManagerId(Long managerId);

    List<Project> findByCreatedById(Long userId);

    List<Project> findByStatus(ProjectStatus status);

    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.status IN :statuses")
    List<Project> findByStatusIn(@Param("statuses") List<ProjectStatus> statuses);

    @Query("SELECT p FROM Project p WHERE " +
           "LOWER(p.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.siteCity) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Project> search(@Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.client.id = :clientId AND " +
           "(LOWER(p.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Project> searchByClient(@Param("clientId") Long clientId, 
                                  @Param("search") String search, 
                                  Pageable pageable);

    // Projets en retard (date de fin dépassée mais pas terminés)
    @Query("SELECT p FROM Project p WHERE p.endDate < :today AND p.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Project> findOverdueProjects(@Param("today") LocalDate today);

    // Projets à venir (démarrage dans les X jours)
    @Query("SELECT p FROM Project p WHERE p.startDate BETWEEN :today AND :futureDate AND p.status = 'PLANNING'")
    List<Project> findUpcomingProjects(@Param("today") LocalDate today, @Param("futureDate") LocalDate futureDate);

    // Statistiques
    long countByStatus(ProjectStatus status);

    @Query("SELECT SUM(p.estimatedBudget) FROM Project p WHERE p.status NOT IN ('CANCELLED')")
    java.math.BigDecimal sumEstimatedBudget();

    @Query("SELECT SUM(p.actualCost) FROM Project p WHERE p.status NOT IN ('CANCELLED')")
    java.math.BigDecimal sumActualCost();

    @Query("SELECT MAX(p.reference) FROM Project p WHERE p.reference LIKE :prefix")
    Optional<String> findLastReferenceByPrefix(@Param("prefix") String prefix);
}

