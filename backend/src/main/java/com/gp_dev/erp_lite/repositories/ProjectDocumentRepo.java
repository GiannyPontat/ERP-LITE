package com.gp_dev.erp_lite.repositories;

import com.gp_dev.erp_lite.models.ProjectDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectDocumentRepo extends JpaRepository<ProjectDocument, Long> {

    List<ProjectDocument> findByProjectId(Long projectId);

    List<ProjectDocument> findByProjectIdAndDocumentType(Long projectId, String documentType);

    long countByProjectId(Long projectId);

    void deleteByProjectId(Long projectId);
}

