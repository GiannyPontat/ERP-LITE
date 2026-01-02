package com.gp_dev.erp_lite.services.impl;

import com.gp_dev.erp_lite.dtos.ClientDto;
import com.gp_dev.erp_lite.dtos.CreateClientDto;
import com.gp_dev.erp_lite.dtos.UpdateClientDto;
import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.Client;
import com.gp_dev.erp_lite.repositories.ClientRepo;
import com.gp_dev.erp_lite.services.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepo clientRepo;
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    @Override
    public Page<ClientDto> findAll(Pageable pageable) {
        return clientRepo.findAll(pageable)
                .map(this::toDto);
    }

    @Override
    public Page<ClientDto> search(String searchTerm, Pageable pageable) {
        return clientRepo.findBySearchTerm(searchTerm, pageable)
                .map(this::toDto);
    }

    @Override
    public ClientDto findById(Long id) {
        Client client = clientRepo.findById(id)
                .orElseThrow(() -> new AppException("Client not found", HttpStatus.NOT_FOUND));
        return toDto(client);
    }

    @Override
    public ClientDto create(CreateClientDto createClientDto) {
        // Vérifier l'unicité du SIRET si fourni
        if (createClientDto.getSiret() != null && !createClientDto.getSiret().trim().isEmpty()) {
            if (clientRepo.findBySiret(createClientDto.getSiret()).isPresent()) {
                throw new AppException("SIRET already exists: " + createClientDto.getSiret(), 
                        HttpStatus.CONFLICT);
            }
        }

        // Vérifier l'email valide si fourni
        if (createClientDto.getEmail() != null && !createClientDto.getEmail().trim().isEmpty()) {
            validateEmail(createClientDto.getEmail());
            
            // Vérifier l'unicité de l'email
            if (clientRepo.findByEmail(createClientDto.getEmail()).isPresent()) {
                throw new AppException("Email already exists: " + createClientDto.getEmail(), 
                        HttpStatus.CONFLICT);
            }
        }

        // Construire le client
        Client client = Client.builder()
                .companyName(createClientDto.getCompanyName())
                .siret(createClientDto.getSiret())
                .contactFirstName(createClientDto.getContactFirstName())
                .contactLastName(createClientDto.getContactLastName())
                .email(createClientDto.getEmail())
                .phone(createClientDto.getPhone())
                .address(createClientDto.getAddress())
                .city(createClientDto.getCity())
                .postalCode(createClientDto.getPostalCode())
                .paymentTerms(createClientDto.getPaymentTerms())
                .notes(createClientDto.getNotes())
                // Champs de compatibilité
                .nom(createClientDto.getNom() != null ? createClientDto.getNom() : createClientDto.getContactLastName())
                .entreprise(createClientDto.getEntreprise() != null ? createClientDto.getEntreprise() : createClientDto.getCompanyName())
                .telephone(createClientDto.getTelephone() != null ? createClientDto.getTelephone() : createClientDto.getPhone())
                .adresse(createClientDto.getAdresse() != null ? createClientDto.getAdresse() : createClientDto.getAddress())
                .build();

        client = clientRepo.save(client);
        log.info("Client created with ID: {}", client.getId());
        
        return toDto(client);
    }

    @Override
    public ClientDto update(Long id, UpdateClientDto updateClientDto) {
        Client client = clientRepo.findById(id)
                .orElseThrow(() -> new AppException("Client not found", HttpStatus.NOT_FOUND));

        // Vérifier l'unicité du SIRET si modifié
        if (updateClientDto.getSiret() != null && !updateClientDto.getSiret().trim().isEmpty()) {
            if (clientRepo.existsBySiretExcludingId(updateClientDto.getSiret(), id)) {
                throw new AppException("SIRET already exists: " + updateClientDto.getSiret(), 
                        HttpStatus.CONFLICT);
            }
            client.setSiret(updateClientDto.getSiret());
        }

        // Vérifier l'email valide si modifié
        if (updateClientDto.getEmail() != null && !updateClientDto.getEmail().trim().isEmpty()) {
            validateEmail(updateClientDto.getEmail());
            
            // Vérifier l'unicité de l'email
            if (clientRepo.existsByEmailExcludingId(updateClientDto.getEmail(), id)) {
                throw new AppException("Email already exists: " + updateClientDto.getEmail(), 
                        HttpStatus.CONFLICT);
            }
            client.setEmail(updateClientDto.getEmail());
        }

        // Mettre à jour les autres champs
        if (updateClientDto.getCompanyName() != null) {
            client.setCompanyName(updateClientDto.getCompanyName());
        }
        if (updateClientDto.getContactFirstName() != null) {
            client.setContactFirstName(updateClientDto.getContactFirstName());
        }
        if (updateClientDto.getContactLastName() != null) {
            client.setContactLastName(updateClientDto.getContactLastName());
        }
        if (updateClientDto.getPhone() != null) {
            client.setPhone(updateClientDto.getPhone());
        }
        if (updateClientDto.getAddress() != null) {
            client.setAddress(updateClientDto.getAddress());
        }
        if (updateClientDto.getCity() != null) {
            client.setCity(updateClientDto.getCity());
        }
        if (updateClientDto.getPostalCode() != null) {
            client.setPostalCode(updateClientDto.getPostalCode());
        }
        if (updateClientDto.getPaymentTerms() != null) {
            client.setPaymentTerms(updateClientDto.getPaymentTerms());
        }
        if (updateClientDto.getNotes() != null) {
            client.setNotes(updateClientDto.getNotes());
        }
        
        // Champs de compatibilité
        if (updateClientDto.getNom() != null) {
            client.setNom(updateClientDto.getNom());
        }
        if (updateClientDto.getEntreprise() != null) {
            client.setEntreprise(updateClientDto.getEntreprise());
        }
        if (updateClientDto.getTelephone() != null) {
            client.setTelephone(updateClientDto.getTelephone());
        }
        if (updateClientDto.getAdresse() != null) {
            client.setAdresse(updateClientDto.getAdresse());
        }

        client = clientRepo.save(client);
        log.info("Client updated with ID: {}", client.getId());
        
        return toDto(client);
    }

    @Override
    public void delete(Long id) {
        if (!clientRepo.existsById(id)) {
            throw new AppException("Client not found", HttpStatus.NOT_FOUND);
        }
        clientRepo.deleteById(id);
        log.info("Client deleted with ID: {}", id);
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new AppException("Email cannot be empty", HttpStatus.BAD_REQUEST);
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new AppException("Invalid email format: " + email, HttpStatus.BAD_REQUEST);
        }
    }

    private ClientDto toDto(Client client) {
        return ClientDto.builder()
                .id(client.getId())
                .companyName(client.getCompanyName())
                .siret(client.getSiret())
                .contactFirstName(client.getContactFirstName())
                .contactLastName(client.getContactLastName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .address(client.getAddress())
                .city(client.getCity())
                .postalCode(client.getPostalCode())
                .paymentTerms(client.getPaymentTerms())
                .notes(client.getNotes())
                .userId(client.getUser() != null ? client.getUser().getId() : null)
                // Champs de compatibilité
                .nom(client.getNom())
                .entreprise(client.getEntreprise())
                .telephone(client.getTelephone())
                .adresse(client.getAdresse())
                .build();
    }
}
