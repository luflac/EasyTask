package com.kahlab.easytask.service;

import com.kahlab.easytask.model.Client;
import com.kahlab.easytask.model.Collaborator;
import com.kahlab.easytask.repository.ClientRepository;
import com.kahlab.easytask.repository.CollaboratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private LogService logService;
    @Autowired
    private CollaboratorRepository collaboratorRepository;

    private String getLoggedUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Client saveOrUpdateClient(Client client) {
        boolean isNew = (client.getIdClient() == null);

        Client savedClient = clientRepository.save(client);

        if (isNew) {
            String loggedEmail = getLoggedUserEmail();
            Collaborator author = collaboratorRepository.findByEmail(loggedEmail)
                    .orElseThrow(() -> new RuntimeException("Colaborador não encontrado"));

            logService.logAction(
                    author.getIdCollaborator(),
                    "CLIENT",
                    "CREATE",
                    "Cliente '" + savedClient.getName() + "' foi criado"
            );
        }

        return savedClient;
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
            existingClient.setCnpj(updatedClient.getCnpj());

            // Salva apenas uma vez
            Client savedClient = clientRepository.save(existingClient);

            // Log da atualização
            String loggedEmail = getLoggedUserEmail();
            Collaborator author = collaboratorRepository.findByEmail(loggedEmail)
                    .orElseThrow(() -> new RuntimeException("Colaborador não encontrado"));

            logService.logAction(
                    author.getIdCollaborator(),
                    "CLIENT",
                    "UPDATE",
                    "Cliente '" + savedClient.getName() + "' foi atualizado"
            );

            return savedClient;
        }).orElseThrow(() -> new RuntimeException("Client not found with ID: " + id));
    }

    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        String loggedEmail = getLoggedUserEmail();
        Collaborator author = collaboratorRepository.findByEmail(loggedEmail)
                .orElseThrow(() -> new RuntimeException("Colaborador não encontrado"));

        // ✅ Log da exclusão
        logService.logAction(
                author.getIdCollaborator(),
                "CLIENT",
                "DELETE",
                "Cliente '" + client.getName() + "' foi excluído"
        );

        clientRepository.delete(client);
    }

}
