package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.ClientDto;
import com.gp_dev.erp_lite.models.Client;

import java.util.List;

public interface ClientService {

    List<ClientDto> all();

    Client byId(Long id);

    Client save(ClientDto vehicle);

    void delete(Long idVehicle);

    Client update(ClientDto vehicle);
}
