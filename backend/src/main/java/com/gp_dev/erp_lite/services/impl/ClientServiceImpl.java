package com.gp_dev.erp_lite.services.impl;

import com.gp_dev.erp_lite.dtos.ClientDto;
import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.Client;
import com.gp_dev.erp_lite.repositories.ClientRepo;
import com.gp_dev.erp_lite.services.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepo repository;

    @Override
    public List<ClientDto> all() {
        return this.repository.findAll().stream()
                .map(Client::dto)
                .collect(Collectors.toList());
    }
    

    @Override
    public Client byId(Long id) {
        return this.repository.findById(id)
                .orElseThrow(() -> new AppException("Client not found", HttpStatus.NOT_FOUND));
    }


    @Override
    public Client save(ClientDto client) {
        return this.repository.saveAndFlush(Client.builder()
                .nom(client.getNom())
                .email(client.getEmail())
                .adresse(client.getAdresse())
                .entreprise(client.getEntreprise())
                .telephone(client.getTelephone())
                .build());
    }


    @Override
    public void delete(Long idClient) {
        this.repository.deleteById(idClient);
    }


    @Override
    public Client update(ClientDto client) {
        Client model = this.repository.findById(client.getId())
                .orElseThrow(() -> new AppException("client not found", HttpStatus.NOT_FOUND));

        model.setAdresse(client.getAdresse());
        model.setNom(client.getNom());
        model.setEmail(client.getEmail());
        model.setEntreprise(client.getEntreprise());
        model.setTelephone(client.getTelephone());

        return this.repository.save(model);
    }
}
