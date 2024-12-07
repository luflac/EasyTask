package com.kahlab.easytask.service;

import com.kahlab.easytask.model.Client;
import com.kahlab.easytask.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public Client saveOrUpdateClient(Client client) {
        return clientRepository.save(client);
    }

    public Optional<Client> findClientById(Long id) {
        return clientRepository.findById(id);
    }

    public List<Client> findAllClients() {
        return clientRepository.findAll();
    }

    public List<Client> findClientsByName(String name) {
        return clientRepository.findByName(name);
    }

    public Client updateClient(Long id, Client updatedClient) {
        return clientRepository.findById(id).map(existingClient -> {
            existingClient.setName(updatedClient.getName());
            existingClient.setEmail(updatedClient.getEmail());
            existingClient.setPhone(updatedClient.getPhone());
            return clientRepository.save(existingClient);
        }).orElseThrow(() -> new RuntimeException("Client not found with ID: " + id));
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
}
