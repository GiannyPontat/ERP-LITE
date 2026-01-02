package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.ClientDto;
import com.gp_dev.erp_lite.dtos.CreateClientDto;
import com.gp_dev.erp_lite.dtos.UpdateClientDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {
    
    Page<ClientDto> findAll(Pageable pageable);
    
    Page<ClientDto> search(String searchTerm, Pageable pageable);
    
    ClientDto findById(Long id);
    
    ClientDto create(CreateClientDto createClientDto);
    
    ClientDto update(Long id, UpdateClientDto updateClientDto);
    
    void delete(Long id);
}
