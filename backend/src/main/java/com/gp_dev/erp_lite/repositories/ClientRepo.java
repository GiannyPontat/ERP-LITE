package com.gp_dev.erp_lite.repositories;

import com.gp_dev.erp_lite.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepo extends JpaRepository<Client, Long> {
}
